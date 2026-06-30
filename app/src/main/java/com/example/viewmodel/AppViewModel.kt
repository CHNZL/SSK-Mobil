package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.*
import com.example.util.AppUpdater
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.Calendar

data class AppNotification(
    val id: String,
    val eventId: Int,
    val eventTitle: String,
    val message: String,
    val type: String, // "ONGOING_MISSING" or "OVERDUE_MISSING"
    val teacherEmails: List<String>
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    // App Version Info
    val currentVersion = "1.0.0"

    sealed class UpdateState {
        object Idle : UpdateState()
        object Checking : UpdateState()
        data class NewVersionAvailable(val version: String, val apkUrl: String, val changelog: String, val isSimulated: Boolean) : UpdateState()
        object NoUpdate : UpdateState()
        data class Downloading(val progress: Int) : UpdateState()
        object DownloadComplete : UpdateState()
        data class Error(val message: String) : UpdateState()
    }

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    init {
        // Automatically clean up any downloaded APK files on startup to free space!
        AppUpdater.cleanUpCachedApks(application)
    }

    fun triggerUpdateCheck(context: android.content.Context) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Checking
            // Artificial delay to make check feel premium and realistic
            delay(1200)
            val result = AppUpdater.checkForUpdates(currentVersion)
            when (result) {
                is AppUpdater.UpdateCheckResult.UpdateAvailable -> {
                    _updateState.value = UpdateState.NewVersionAvailable(
                        version = result.latestVersion,
                        apkUrl = result.downloadUrl,
                        changelog = result.changelog,
                        isSimulated = false
                    )
                }
                AppUpdater.UpdateCheckResult.NoUpdate -> {
                    _updateState.value = UpdateState.NoUpdate
                }
                is AppUpdater.UpdateCheckResult.Error -> {
                    _updateState.value = UpdateState.Error(result.message)
                }
            }
        }
    }

    fun triggerSimulatedUpdate() {
        _updateState.value = UpdateState.NewVersionAvailable(
            version = "1.1.0",
            apkUrl = "https://github.com/cihanozel10/okul-paylasim/releases/download/v1.1.0/app-debug.apk",
            changelog = "• Otomatik güncelleme sistemi entegre edildi.\n• GitHub Actions entegrasyonu tamamlandı.\n• Performans iyileştirmeleri ve hata gidermeleri yapıldı.",
            isSimulated = true
        )
    }

    fun startDownloadingAndInstall(context: android.content.Context, apkUrl: String, isSimulated: Boolean) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Downloading(0)
            if (isSimulated) {
                // Smoothly simulate download progress on emulator
                for (progress in 0..100 step 10) {
                    delay(250)
                    _updateState.value = UpdateState.Downloading(progress)
                }
                delay(400)
                _updateState.value = UpdateState.DownloadComplete
                
                // Write a dummy apk so that FileProvider and install activity can be triggered as real test
                try {
                    val dummyFile = java.io.File(context.cacheDir, "okul_paylasim_update.apk")
                    dummyFile.writeText("simulated apk content")
                    AppUpdater.installApk(context, dummyFile)
                } catch (e: Exception) {
                    _updateState.value = UpdateState.Error("Simülasyon yükleme hatası: ${e.localizedMessage}")
                }
            } else {
                val downloadedFile = AppUpdater.downloadApk(context, apkUrl) { progress ->
                    _updateState.value = UpdateState.Downloading(progress)
                }
                if (downloadedFile != null && downloadedFile.exists()) {
                    _updateState.value = UpdateState.DownloadComplete
                    AppUpdater.installApk(context, downloadedFile)
                } else {
                    _updateState.value = UpdateState.Error("Dosya indirme başarısız oldu. Lütfen internet bağlantınızı kontrol edin.")
                }
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = UpdateState.Idle
    }

    private val database: AppDatabase = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java,
        "okul_paylasim_db"
    )
    .fallbackToDestructiveMigration()
    .build()

    val repository: AppRepository = AppRepository(database.appDao())

    // UI Navigation State (Simple Screen Stack Router)
    sealed class Screen {
        object Login : Screen()
        object Dashboard : Screen()
        object EventList : Screen()
        data class EventDetail(val eventId: Int) : Screen()
        object UserManagement : Screen()
        object AddEvent : Screen()
    }

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Login)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenHistory = mutableListOf<Screen>()

    fun navigateTo(screen: Screen) {
        screenHistory.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack() {
        if (screenHistory.isNotEmpty()) {
            _currentScreen.value = screenHistory.removeAt(screenHistory.size - 1)
        } else {
            _currentScreen.value = Screen.Dashboard
        }
    }

    // Auth State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // Database Flows
    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvents: StateFlow<List<EventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAssignments: StateFlow<List<EventAssignmentEntity>> = repository.allAssignments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMedia: StateFlow<List<MediaItemEntity>> = repository.allMedia
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Time Simulation State
    private val _simulatedDate = MutableStateFlow(System.currentTimeMillis())
    val simulatedDate: StateFlow<Long> = _simulatedDate.asStateFlow()

    // Reactive Notification Flow
    val notifications: StateFlow<List<AppNotification>> = combine(
        allEvents,
        allAssignments,
        allMedia,
        simulatedDate
    ) { events, assignments, media, simDate ->
        val list = mutableListOf<AppNotification>()
        
        events.forEach { event ->
            // Check if there is any media uploaded for this event
            val hasMedia = media.any { it.eventId == event.id }
            
            if (!hasMedia) {
                val assignedTeachers = assignments
                    .filter { it.eventId == event.id }
                    .map { it.teacherEmail }

                // Case 1: Overdue Missing (Event ended, no media)
                if (simDate > event.endDate) {
                    list.add(
                        AppNotification(
                            id = "overdue_${event.id}",
                            eventId = event.id,
                            eventTitle = event.title,
                            message = "⚠️ EKSİK BELGE: '${event.title}' etkinliği sona erdi ancak henüz hiçbir görsel/belge eklenmedi! Unutulmuş olabilir, lütfen acilen yükleyin.",
                            type = "OVERDUE_MISSING",
                            teacherEmails = assignedTeachers
                        )
                    )
                }
                // Case 2: Ongoing Missing (Event is active currently, no media)
                else if (simDate >= event.startDate && simDate <= event.endDate) {
                    list.add(
                        AppNotification(
                            id = "ongoing_${event.id}",
                            eventId = event.id,
                            eventTitle = event.title,
                            message = "📢 GÖRSEL BEKLENİYOR: '${event.title}' etkinliği devam ediyor. Web sitesi için görsel ve belgelerinizi lütfen bu tarih aralığında sisteme yükleyin.",
                            type = "ONGOING_MISSING",
                            teacherEmails = assignedTeachers
                        )
                    )
                }
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Pre-populate data if DB is empty
        viewModelScope.launch {
            allUsers.collectLatest { users ->
                if (users.isEmpty()) {
                    prepopulateDatabase()
                }
            }
        }
    }

    private suspend fun prepopulateDatabase() {
        // Default Authorized Users
        val users = listOf(
            UserEntity("cihan.ozel10@gmail.com", "Cihan Özel", "ADMIN"),
            UserEntity("admin@okul.com", "Ahmet Yılmaz (Müdür)", "ADMIN"),
            UserEntity("ayse@okul.com", "Ayşe Kaya (Edebiyat Öğrt.)", "TEACHER"),
            UserEntity("mehmet@okul.com", "Mehmet Demir (Beden Eğt. Öğrt.)", "TEACHER"),
            UserEntity("fatma@okul.com", "Fatma Şahin (Müzik Öğrt.)", "TEACHER"),
            UserEntity("zeynep@okul.com", "Zeynep Çelik (Resim Öğrt.)", "TEACHER")
        )
        users.forEach { repository.insertUser(it) }

        // Sample Events
        val cal = Calendar.getInstance()
        
        // 1. Past event (Needs photos but has none -> triggers overdue warning)
        cal.add(Calendar.DAY_OF_YEAR, -10)
        val pastStart = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 2)
        val pastEnd = cal.timeInMillis

        val event1Id = repository.insertEvent(
            EventEntity(
                title = "29 Ekim Cumhuriyet Bayramı Töreni",
                description = "Okul bahçesinde gerçekleştirilen Cumhuriyet Bayramı resmi kutlama töreni.",
                startDate = pastStart,
                endDate = pastEnd
            )
        ).toInt()
        repository.assignTeacherToEvent(event1Id, "zeynep@okul.com")
        repository.assignTeacherToEvent(event1Id, "fatma@okul.com")

        // 2. Active event (ongoing, no photos yet -> triggers ongoing notice)
        val ongoingStart = System.currentTimeMillis() - 1000 * 60 * 60 * 12 // started 12 hours ago
        val ongoingEnd = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3 // ends in 3 days

        val event2Id = repository.insertEvent(
            EventEntity(
                title = "TÜBİTAK 4006 Bilim Şenliği",
                description = "Öğrencilerimizin hazırladığı fen ve teknoloji projelerinin sergilenmesi.",
                startDate = ongoingStart,
                endDate = ongoingEnd
            )
        ).toInt()
        repository.assignTeacherToEvent(event2Id, "ayse@okul.com")
        repository.assignTeacherToEvent(event2Id, "mehmet@okul.com")

        // 3. Completed event (has photos -> no warnings)
        cal.timeInMillis = System.currentTimeMillis()
        cal.add(Calendar.DAY_OF_YEAR, -5)
        val pastStart2 = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 2)
        val pastEnd2 = cal.timeInMillis

        val event3Id = repository.insertEvent(
            EventEntity(
                title = "Sınıflar Arası Futbol Turnuvası",
                description = "Öğrencilerin okul sahasındaki spor şenlikleri turnuva finali.",
                startDate = pastStart2,
                endDate = pastEnd2
            )
        ).toInt()
        repository.assignTeacherToEvent(event3Id, "mehmet@okul.com")
        
        // Insert sample media for this completed event so it doesn't trigger alerts
        repository.insertMedia(
            MediaItemEntity(
                eventId = event3Id,
                filePath = "ic_futbol_turnuvasi",
                isDocument = false,
                fileName = "final_maci_kupa_toreni.jpg",
                uploadedByEmail = "mehmet@okul.com",
                uploadedByName = "Mehmet Demir",
                uploadTimestamp = pastEnd2 - 1000 * 60 * 60 * 2
            )
        )
    }

    // Simulated Time Actions
    fun updateSimulatedDate(daysOffset: Int) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysOffset)
        _simulatedDate.value = cal.timeInMillis
    }

    fun resetSimulatedDate() {
        _simulatedDate.value = System.currentTimeMillis()
    }

    // Auth Actions
    fun login(email: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email.trim().lowercase())
            if (user != null) {
                _currentUser.value = user
                _loginError.value = null
                _currentScreen.value = Screen.Dashboard
            } else {
                _loginError.value = "Bu e-posta adresi sistemde tanımlı değil. Lütfen yöneticinizden yetkilendirme isteyin."
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _loginError.value = null
        _currentScreen.value = Screen.Login
        screenHistory.clear()
    }

    fun clearLoginError() {
        _loginError.value = null
    }

    // Admin: User Actions
    fun addUser(email: String, name: String, role: String) {
        viewModelScope.launch {
            repository.insertUser(UserEntity(email.trim().lowercase(), name.trim(), role))
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            // Protect current user from self-deletion
            if (user.email != _currentUser.value?.email) {
                repository.deleteUser(user)
            }
        }
    }

    // Admin: Event Actions
    fun createEvent(title: String, description: String, startDate: Long, endDate: Long, assignedEmails: List<String>) {
        viewModelScope.launch {
            val eventId = repository.insertEvent(
                EventEntity(
                    title = title.trim(),
                    description = description.trim(),
                    startDate = startDate,
                    endDate = endDate
                )
            ).toInt()
            
            assignedEmails.forEach { email ->
                repository.assignTeacherToEvent(eventId, email)
            }
        }
    }

    fun deleteEvent(eventId: Int) {
        viewModelScope.launch {
            repository.deleteEvent(eventId)
        }
    }

    fun addAssignment(eventId: Int, email: String) {
        viewModelScope.launch {
            repository.assignTeacherToEvent(eventId, email)
        }
    }

    fun removeAssignment(eventId: Int, email: String) {
        viewModelScope.launch {
            repository.removeTeacherFromEvent(eventId, email)
        }
    }

    // Media Actions
    fun uploadMediaItem(eventId: Int, fileName: String, isDocument: Boolean, customPath: String = "") {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            // Create a custom simulated path
            val path = if (customPath.isNotEmpty()) customPath else {
                if (isDocument) "doc_placeholder" else "img_placeholder"
            }
            repository.insertMedia(
                MediaItemEntity(
                    eventId = eventId,
                    filePath = path,
                    isDocument = isDocument,
                    fileName = fileName,
                    uploadedByEmail = user.email,
                    uploadedByName = user.name,
                    uploadTimestamp = _simulatedDate.value // match simulated date for consistency
                )
            )
        }
    }

    fun deleteMediaItem(mediaId: Int) {
        val user = _currentUser.value ?: return
        if (user.role == "ADMIN") {
            viewModelScope.launch {
                repository.deleteMedia(mediaId)
            }
        }
    }
}
