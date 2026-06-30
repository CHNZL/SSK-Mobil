package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.viewmodel.AppNotification
import com.example.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

// Helper: Format date in Turkish locale
fun formatDateTr(timestamp: Long): String {
    val sdf = SimpleDateFormat("d MMMM yyyy", Locale("tr"))
    return sdf.format(Date(timestamp))
}

@Composable
fun AppNavigationWrapper(viewModel: AppViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
            when (screen) {
                is AppViewModel.Screen.Login -> LoginScreen(viewModel)
                is AppViewModel.Screen.Dashboard -> DashboardScreen(viewModel)
                is AppViewModel.Screen.EventList -> EventListScreen(viewModel)
                is AppViewModel.Screen.EventDetail -> EventDetailScreen(viewModel, screen.eventId)
                is AppViewModel.Screen.UserManagement -> UserManagementScreen(viewModel)
                is AppViewModel.Screen.AddEvent -> AddEventScreen(viewModel)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 1. LOGIN SCREEN
// ---------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: AppViewModel) {
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    
    var showAccountChooser by remember { mutableStateOf(false) }
    var showCustomEmailDialog by remember { mutableStateOf(false) }
    var customEmailInput by remember { mutableStateOf("") }
    var customEmailError by remember { mutableStateOf<String?>(null) }
    
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.background
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 450.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant School / Photo Network App Icon / Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Okul Paylaşım Logo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Okul Paylaşım Ağı",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Okul web sitesi için görsel ve belge koordinasyon portalı",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Google Branding
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text("o", color = Color(0xFFEA4335), fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text("o", color = Color(0xFFFBBC05), fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text("g", color = Color(0xFF4285F4), fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text("l", color = Color(0xFF34A853), fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text("e", color = Color(0xFFEA4335), fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    }

                    Text(
                        text = "Gmail Hesabı ile Giriş",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Bu portala sadece yetkilendirilmiş Gmail hesapları ile güvenli giriş yapılabilir.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Authentic Google Sign-In Button
                    OutlinedButton(
                        onClick = { showAccountChooser = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("login_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1F1F1F)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Google "G" representation
                            Text(
                                text = "G",
                                color = Color(0xFF4285F4),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 4.dp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Google ile Giriş Yapın",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1F1F1F)
                            )
                        }
                    }

                    if (loginError != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = loginError ?: "",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Help info
            Text(
                text = "Giriş yapamıyor musunuz? Okul yöneticinizden e-posta adresinizin sisteme eklenmesini isteyin.",
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }

    // SIMULATED GOOGLE ACCOUNT SELECTOR DIALOG
    if (showAccountChooser) {
        Dialog(onDismissRequest = { showAccountChooser = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Google logo header in chooser
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("o", color = Color(0xFFEA4335), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("o", color = Color(0xFFFBBC05), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("g", color = Color(0xFF4285F4), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("l", color = Color(0xFF34A853), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("e", color = Color(0xFFEA4335), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    Text(
                        text = "Bir hesap seçin",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Okul Paylaşım uygulamasına devam etmek için",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 16.dp)
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Simulated device Google accounts list
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showAccountChooser = false
                                    viewModel.login("cihan.ozel10@gmail.com")
                                }
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar circle with first letter or photo icon
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "C",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Cihan Özel",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "cihan.ozel10@gmail.com",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }

                            // Device Account badge
                            Text(
                                text = "Aktif Hesap",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }

                    // Use another account row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showAccountChooser = false
                                showCustomEmailDialog = true
                            }
                            .padding(vertical = 14.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Account",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Başka bir hesap kullanın",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Güvenliğiniz için devam etmeden önce Google, cihazınızın bu uygulama ile paylaşacağı bilgileri doğrular.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // CUSTOM EMAIL INPUT DIALOG
    if (showCustomEmailDialog) {
        Dialog(onDismissRequest = { showCustomEmailDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Google logo header
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("o", color = Color(0xFFEA4335), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("o", color = Color(0xFFFBBC05), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("g", color = Color(0xFF4285F4), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("l", color = Color(0xFF34A853), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("e", color = Color(0xFFEA4335), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    Text(
                        text = "Google Hesabı Ekle",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Gmail veya okul e-posta adresinizi yazın",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 20.dp)
                    )

                    OutlinedTextField(
                        value = customEmailInput,
                        onValueChange = {
                            customEmailInput = it
                            customEmailError = null
                        },
                        label = { Text("Gmail Adresi") },
                        placeholder = { Text("adiniz@gmail.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = "Email")
                        },
                        isError = customEmailError != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (customEmailError != null) {
                        Text(
                            text = customEmailError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                showCustomEmailDialog = false
                                customEmailInput = ""
                                customEmailError = null
                            }
                        ) {
                            Text("İptal")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                val trimmed = customEmailInput.trim().lowercase()
                                if (trimmed.isEmpty()) {
                                    customEmailError = "E-posta alanı boş bırakılamaz."
                                } else if (!trimmed.endsWith("@gmail.com") && !trimmed.endsWith("@okul.com")) {
                                    customEmailError = "Lütfen geçerli bir Gmail adresi girin (örn: ad@gmail.com)."
                                } else {
                                    showCustomEmailDialog = false
                                    viewModel.login(trimmed)
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("İlerle")
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 2. DASHBOARD SCREEN
// ---------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allEvents by viewModel.allEvents.collectAsStateWithLifecycle()
    val allMedia by viewModel.allMedia.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val simulatedDate by viewModel.simulatedDate.collectAsStateWithLifecycle()
    val updateState by viewModel.updateState.collectAsStateWithLifecycle()

    var showSimulateDialog by remember { mutableStateOf(false) }

    // Elegant Dialog to manage Update Flow
    if (updateState !is AppViewModel.UpdateState.Idle) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { viewModel.resetUpdateState() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (val state = updateState) {
                        is AppViewModel.UpdateState.Checking -> {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Güncelleme Denetleniyor...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Lütfen bekleyin, en son sürüm sorgulanıyor.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                        is AppViewModel.UpdateState.NewVersionAvailable -> {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = "Güncelleme Var",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Yeni Sürüm Hazır! (v${state.version})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Changelog box
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = "Yenilikler:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = state.changelog,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(onClick = { viewModel.resetUpdateState() }) {
                                    Text("Daha Sonra")
                                }
                                Button(
                                    onClick = {
                                        viewModel.startDownloadingAndInstall(context, state.apkUrl, state.isSimulated)
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.CloudDownload, contentDescription = "Download")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Şimdi Güncelle")
                                }
                            }
                        }
                        is AppViewModel.UpdateState.NoUpdate -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Güncel",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Uygulamanız Güncel!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "En son sürümü kullanıyorsunuz (v${viewModel.currentVersion}).",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.resetUpdateState() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Tamam")
                            }
                        }
                        is AppViewModel.UpdateState.Downloading -> {
                            CircularProgressIndicator(
                                progress = { state.progress / 100f },
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Güncelleme İndiriliyor... %${state.progress}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { state.progress / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Lütfen indirme bitene kadar uygulamayı kapatmayın.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                        is AppViewModel.UpdateState.DownloadComplete -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "İndirme Tamamlandı",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "İndirme Tamamlandı!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Paket kurucu başlatılıyor. Lütfen güncellemeyi onaylayın.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.resetUpdateState() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Kapat")
                            }
                        }
                        is AppViewModel.UpdateState.Error -> {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Hata",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Güncelleme Denetlenemedi",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            // Premium feature: provide dynamic simulation button in error dialog
                            // so that users on the AI Studio platform can easily see the update flow!
                            Button(
                                onClick = { viewModel.triggerSimulatedUpdate() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Simulate")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Simülasyon Modunu Başlat")
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(onClick = { viewModel.resetUpdateState() }) {
                                    Text("Kapat")
                                }
                                Button(
                                    onClick = { viewModel.triggerUpdateCheck(context) },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Yeniden Dene")
                                }
                            }
                        }
                        AppViewModel.UpdateState.Idle -> {}
                    }
                }
            }
        }
    }

    // Filter notifications based on role
    val userNotifications = remember(notifications, currentUser) {
        val user = currentUser
        if (user == null) emptyList()
        else if (user.role == "ADMIN") {
            notifications
        } else {
            // Regular teacher only sees warnings about events they are assigned to
            notifications.filter { it.teacherEmails.contains(user.email) }
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hoş Geldiniz,",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currentUser?.name ?: "Ziyaretçi",
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSimulateDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Simülasyon",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Çıkış Yap"
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                activeTab = "dashboard",
                onTabSelect = { tab ->
                    when (tab) {
                        "events" -> viewModel.navigateTo(AppViewModel.Screen.EventList)
                        "users" -> viewModel.navigateTo(AppViewModel.Screen.UserManagement)
                    }
                },
                isAdmin = currentUser?.role == "ADMIN"
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 🚀 System Version & Live Update Controller Panel
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Version Logo",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Sistem Sürümü",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = "v${viewModel.currentVersion}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        
                        // Compact interactive update icon trigger
                        OutlinedButton(
                            onClick = { viewModel.triggerUpdateCheck(context) },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Güncelleme Kontrol Et",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Güncelle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Simulated Time Status Panel
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "📅 Simüle Edilen Tarih",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = formatDateTr(simulatedDate),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Button(
                            onClick = { showSimulateDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Tarihi Değiştir", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Toplam Etkinlik",
                        value = allEvents.size.toString(),
                        icon = Icons.Default.Event,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Toplam Paylaşım",
                        value = allMedia.size.toString(),
                        icon = Icons.Default.CloudUpload,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Warnings and Notifications Section
            item {
                Text(
                    text = "Sistem Bildirimleri (${userNotifications.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            if (userNotifications.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Tüm belgeler tamam",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Harika! Hiçbir eksik belge veya görsel uyarısı bulunmuyor.",
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(userNotifications, key = { it.id }) { notification ->
                    NotificationItemCard(notification) {
                        viewModel.navigateTo(AppViewModel.Screen.EventDetail(notification.eventId))
                    }
                }
            }

            // Quick Actions or shortcuts
            item {
                Text(
                    text = "Hızlı İşlemler",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickActionRow(
                        title = "Etkinlikleri İncele",
                        subtitle = "Tüm okul etkinliklerini ve görevli sorumluları gör",
                        icon = Icons.Default.ListAlt,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        viewModel.navigateTo(AppViewModel.Screen.EventList)
                    }

                    if (currentUser?.role == "ADMIN") {
                        QuickActionRow(
                            title = "Yeni Etkinlik Tanımla",
                            subtitle = "Öğretmen ataması yaparak etkinlik oluştur",
                            icon = Icons.Default.AddBox,
                            color = MaterialTheme.colorScheme.secondary
                        ) {
                            viewModel.navigateTo(AppViewModel.Screen.AddEvent)
                        }

                        QuickActionRow(
                            title = "Öğretmen Yetkilendirme",
                            subtitle = "Gmail adreslerini tanımla ve yöneticileri yönet",
                            icon = Icons.Default.People,
                            color = MaterialTheme.colorScheme.tertiary
                        ) {
                            viewModel.navigateTo(AppViewModel.Screen.UserManagement)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Interactive Date Simulator dialog
    if (showSimulateDialog) {
        DateSimulationDialog(
            currentSimDate = simulatedDate,
            onClose = { showSimulateDialog = false },
            onUpdateDate = { offset ->
                viewModel.updateSimulatedDate(offset)
                showSimulateDialog = false
            },
            onResetDate = {
                viewModel.resetSimulatedDate()
                showSimulateDialog = false
            }
        )
    }
}

// ---------------------------------------------------------------------------
// 3. EVENT LIST SCREEN
// ---------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allEvents by viewModel.allEvents.collectAsStateWithLifecycle()
    val allAssignments by viewModel.allAssignments.collectAsStateWithLifecycle()
    val allMedia by viewModel.allMedia.collectAsStateWithLifecycle()
    val simulatedDate by viewModel.simulatedDate.collectAsStateWithLifecycle()

    var filterOnlyMine by remember { mutableStateOf(false) }

    // Determine status of an event
    fun getEventStatus(event: EventEntity): String {
        val hasMedia = allMedia.any { it.eventId == event.id }
        return when {
            hasMedia -> "TAMAMLANDI"
            simulatedDate > event.endDate -> "EKSIK"
            simulatedDate >= event.startDate && simulatedDate <= event.endDate -> "DEVAM_EDIYOR"
            else -> "GELECEK"
        }
    }

    // Filtered Events list
    val filteredEvents = remember(allEvents, allAssignments, currentUser, filterOnlyMine) {
        val user = currentUser
        if (filterOnlyMine && user != null) {
            val assignedEventIds = allAssignments
                .filter { it.teacherEmail == user.email }
                .map { it.eventId }
            allEvents.filter { assignedEventIds.contains(it.id) }
        } else {
            allEvents
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Okul Etkinlikleri", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    if (currentUser?.role == "ADMIN") {
                        IconButton(onClick = { viewModel.navigateTo(AppViewModel.Screen.AddEvent) }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Yeni Etkinlik")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                activeTab = "events",
                onTabSelect = { tab ->
                    when (tab) {
                        "dashboard" -> viewModel.navigateTo(AppViewModel.Screen.Dashboard)
                        "users" -> viewModel.navigateTo(AppViewModel.Screen.UserManagement)
                    }
                },
                isAdmin = currentUser?.role == "ADMIN"
            )
        },
        floatingActionButton = {
            if (currentUser?.role == "ADMIN") {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.navigateTo(AppViewModel.Screen.AddEvent) },
                    icon = { Icon(Icons.Default.Add, "Ekle") },
                    text = { Text("Etkinlik Ekle") },
                    modifier = Modifier.testTag("add_event_fab")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = !filterOnlyMine,
                    onClick = { filterOnlyMine = false },
                    label = { Text("Tüm Etkinlikler") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = filterOnlyMine,
                    onClick = { filterOnlyMine = true },
                    label = { Text("Görevli Olduklarım") }
                )
            }

            if (filteredEvents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = "Yok",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (filterOnlyMine) "Görevlendirildiğiniz herhangi bir aktif etkinlik bulunamadı." 
                                   else "Kayıtlı okul etkinliği bulunmuyor.",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredEvents, key = { it.id }) { event ->
                        val status = getEventStatus(event)
                        val assignments = allAssignments.filter { it.eventId == event.id }
                        val isUserAssigned = assignments.any { it.teacherEmail == currentUser?.email }
                        
                        EventItemCard(
                            event = event,
                            status = status,
                            assignedCount = assignments.size,
                            isAssignedToCurrentUser = isUserAssigned,
                            onClick = { viewModel.navigateTo(AppViewModel.Screen.EventDetail(event.id)) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) } // extra padding for FAB
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 4. EVENT DETAIL SCREEN
// ---------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(viewModel: AppViewModel, eventId: Int) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allEvents by viewModel.allEvents.collectAsStateWithLifecycle()
    val allAssignments by viewModel.allAssignments.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allMedia by viewModel.allMedia.collectAsStateWithLifecycle()
    val simulatedDate by viewModel.simulatedDate.collectAsStateWithLifecycle()

    val event = remember(allEvents, eventId) { allEvents.find { it.id == eventId } }
    val mediaItems = remember(allMedia, eventId) { allMedia.filter { it.eventId == eventId } }
    
    // Assigned teachers list
    val assignedTeachers = remember(allAssignments, allUsers, eventId) {
        val emails = allAssignments.filter { it.eventId == eventId }.map { it.teacherEmail }
        allUsers.filter { emails.contains(it.email) }
    }

    val isUserAssigned = remember(assignedTeachers, currentUser) {
        assignedTeachers.any { it.email == currentUser?.email }
    }

    val canUpload = currentUser?.role == "ADMIN" || isUserAssigned

    var showUploadDialog by remember { mutableStateOf(false) }

    if (event == null) {
        Scaffold { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Etkinlik bulunamadı.")
            }
        }
        return
    }

    // Determine event status code
    val isPast = simulatedDate > event.endDate
    val isOngoing = simulatedDate >= event.startDate && simulatedDate <= event.endDate
    val hasFiles = mediaItems.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Etkinlik Detayı", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    if (currentUser?.role == "ADMIN") {
                        IconButton(
                            onClick = {
                                viewModel.deleteEvent(event.id)
                                viewModel.navigateBack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = "Etkinliği Sil",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Event Core Information Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Status Badge
                            val badgeColor = when {
                                hasFiles -> Color(0xFF2E7D32) // Success Green
                                isPast -> Color(0xFFC62828) // Urgent Red
                                isOngoing -> Color(0xFFEF6C00) // Warning Orange
                                else -> Color(0xFF1565C0) // Future Blue
                            }
                            val badgeText = when {
                                hasFiles -> "✓ Tamamlandı"
                                isPast -> "⚠️ Eksik Belge / Unutulmuş"
                                isOngoing -> "📢 Paylaşım Bekleniyor"
                                else -> "🗓️ Gelecek Etkinlik"
                            }
                            
                            Surface(
                                color = badgeColor,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = badgeText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            // Indicator if current user is responsible
                            if (isUserAssigned) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Sorumlusunuz",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = event.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = event.description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dates Layout
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Başlangıç Tarihi",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = formatDateTr(event.startDate),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Bitiş Tarihi",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = formatDateTr(event.endDate),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Responsible Teachers Header & List
            item {
                Text(
                    text = "Görevli Sorumlu Öğretmenler (${assignedTeachers.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (assignedTeachers.isEmpty()) {
                item {
                    Text(
                        text = "Hiçbir öğretmen görevlendirilmedi. (Yalnızca yöneticiler görsel yükleyebilir)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            assignedTeachers.forEachIndexed { index, teacher ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.secondaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = teacher.name.take(1).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = teacher.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = teacher.email,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                                if (index < assignedTeachers.size - 1) {
                                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                }
                            }
                        }
                    }
                }
            }

            // Media Files Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Yüklenen Görsel ve Belgeler (${mediaItems.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (canUpload) {
                        Button(
                            onClick = { showUploadDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp).testTag("upload_media_button")
                        ) {
                            Icon(Icons.Default.Upload, "Yükle", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Yükle", fontSize = 12.sp)
                        }
                    }
                }
            }

            if (mediaItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = "Yükleme yok",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (canUpload) "Henüz hiçbir dosya eklenmemiş. Web sitesi koordinatörüne ulaştırmak üzere ilk görseli veya belgeyi yukarıdan yükleyebilirsiniz!"
                                       else "Bu etkinlik için henüz hiçbir görsel veya belge yüklenmemiş.",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(mediaItems, key = { it.id }) { item ->
                    MediaItemRow(
                        mediaItem = item,
                        isAdmin = currentUser?.role == "ADMIN",
                        onDelete = { viewModel.deleteMediaItem(item.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showUploadDialog) {
        UploadSimulatedFileDialog(
            eventTitle = event.title,
            onClose = { showUploadDialog = false },
            onUpload = { name, isDoc, customPath ->
                viewModel.uploadMediaItem(event.id, name, isDoc, customPath)
                showUploadDialog = false
            }
        )
    }
}

// ---------------------------------------------------------------------------
// 5. USER MANAGEMENT SCREEN
// ---------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    var showAddUserDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Öğretmen Yetkilendirme", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                activeTab = "users",
                onTabSelect = { tab ->
                    when (tab) {
                        "dashboard" -> viewModel.navigateTo(AppViewModel.Screen.Dashboard)
                        "events" -> viewModel.navigateTo(AppViewModel.Screen.EventList)
                    }
                },
                isAdmin = currentUser?.role == "ADMIN"
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddUserDialog = true },
                icon = { Icon(Icons.Default.PersonAdd, "Ekle") },
                text = { Text("Öğretmen Ekle") },
                modifier = Modifier.testTag("add_user_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Bilgi",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Yalnızca bu listede tanımlı olan Gmail adresleri sisteme giriş yapabilir ve kendilerine atanan etkinlikleri yönetebilir.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            items(allUsers, key = { it.email }) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (user.role == "ADMIN") MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.secondaryContainer
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = if (user.role == "ADMIN") MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.email,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        // Role badge
                        Surface(
                            color = if (user.role == "ADMIN") MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (user.role == "ADMIN") "Yönetici" else "Öğretmen",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Delete button (Prevent deleting current logged in user)
                        IconButton(
                            onClick = { viewModel.deleteUser(user) },
                            enabled = user.email != currentUser?.email
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Kullanıcıyı Yetkisizleştir",
                                tint = if (user.email == currentUser?.email) 
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                else 
                                    MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }

    if (showAddUserDialog) {
        AddUserDialog(
            onClose = { showAddUserDialog = false },
            onAdd = { email, name, role ->
                viewModel.addUser(email, name, role)
                showAddUserDialog = false
            }
        )
    }
}

// ---------------------------------------------------------------------------
// 6. ADD EVENT SCREEN
// ---------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(viewModel: AppViewModel) {
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    // Choose start and end dates relative to today for convenience
    var startOffsetDays by remember { mutableStateOf(0) }
    var durationDays by remember { mutableStateOf(3) }

    val teachers = remember(allUsers) { allUsers.filter { it.role != "ADMIN" } }
    val selectedTeachers = remember { mutableStateListOf<String>() }

    // Computes timestamps based on current time + offsets
    val computedStartDate = remember(startOffsetDays) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, startOffsetDays)
        cal.timeInMillis
    }
    
    val computedEndDate = remember(startOffsetDays, durationDays) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, startOffsetDays + durationDays)
        cal.timeInMillis
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yeni Etkinlik Oluştur", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Etkinlik Başlığı") },
                    placeholder = { Text("Örn: Cumhuriyet Koşusu, Müzik Gecesi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("event_title_input")
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Etkinlik Açıklaması") },
                    placeholder = { Text("Etkinliğin detayları, hangi alanlar için görsel gerektiği...") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("event_desc_input")
                )
            }

            // Quick Date Offsets (Highly usable and robust, prevents picker crashes!)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🗓️ Tarih Aralığı Belirleme",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Start date picker simulator
                        Text(
                            text = "Başlangıç Günü: ${
                                when (startOffsetDays) {
                                    0 -> "Bugün"
                                    -5 -> "5 Gün Önce (Geçmiş)"
                                    -10 -> "10 Gün Önce (Geçmiş)"
                                    5 -> "5 Gün Sonra (Gelecek)"
                                    else -> "$startOffsetDays Gün Sonra"
                                }
                            } (${formatDateTr(computedStartDate)})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(-10, -5, 0, 5).forEach { offset ->
                                InputChip(
                                    selected = startOffsetDays == offset,
                                    onClick = { startOffsetDays = offset },
                                    label = { Text(if (offset == 0) "Bugün" else if (offset < 0) "${-offset}g önce" else "${offset}g sonra") }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Duration selector
                        Text(
                            text = "Etkinlik Süresi: $durationDays Gün (Bitiş: ${formatDateTr(computedEndDate)})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(1, 3, 5, 7, 10).forEach { days ->
                                InputChip(
                                    selected = durationDays == days,
                                    onClick = { durationDays = days },
                                    label = { Text("$days Gün") }
                                )
                            }
                        }
                    }
                }
            }

            // Teacher Assign list
            item {
                Text(
                    text = "Görevli Sorumlu Öğretmenleri Seçin",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (teachers.isEmpty()) {
                item {
                    Text(
                        text = "Sistemde kayıtlı öğretmen bulunamadı. Lütfen önce yetkilendirme listesine öğretmen ekleyin.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            } else {
                items(teachers) { teacher ->
                    val isSelected = selectedTeachers.contains(teacher.email)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isSelected) selectedTeachers.remove(teacher.email)
                                else selectedTeachers.add(teacher.email)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                if (checked == true) selectedTeachers.add(teacher.email)
                                else selectedTeachers.remove(teacher.email)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = teacher.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = teacher.email,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            // Action Button
            item {
                Button(
                    onClick = {
                        if (title.isNotEmpty()) {
                            viewModel.createEvent(
                                title = title,
                                description = description,
                                startDate = computedStartDate,
                                endDate = computedEndDate,
                                assignedEmails = selectedTeachers.toList()
                            )
                            viewModel.navigateBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_event_button"),
                    enabled = title.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Etkinliği Kaydet ve Görevlendir")
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// SUB-COMPONENTS & DIALOGS
// ---------------------------------------------------------------------------

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun NotificationItemCard(notification: AppNotification, onClick: () -> Unit) {
    val isUrgent = notification.type == "OVERDUE_MISSING"
    val containerColor = if (isUrgent) {
        Color(0xFFFEEBEE) // Soft Red
    } else {
        Color(0xFFFFF3E0) // Soft Orange
    }
    
    val borderColor = if (isUrgent) Color(0xFFEF9A9A) else Color(0xFFFFCC80)
    val contentColor = if (isUrgent) Color(0xFFC62828) else Color(0xFFE65100)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (isUrgent) Icons.Default.Warning else Icons.Default.NotificationsActive,
                contentDescription = "Uyarı",
                tint = contentColor,
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isUrgent) "Eksik Belge Uyarısı" else "Görsel Bekleniyor",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = Color.Black,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Görsel/belge eklemek için tıklayın →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun EventItemCard(
    event: EventEntity,
    status: String,
    assignedCount: Int,
    isAssignedToCurrentUser: Boolean,
    onClick: () -> Unit
) {
    val (statusColor, statusText, statusIcon) = when (status) {
        "TAMAMLANDI" -> Triple(Color(0xFF2E7D32), "Başarıyla Tamamlandı", Icons.Default.CheckCircle)
        "EKSIK" -> Triple(Color(0xFFC62828), "EKSİK BELGE / UNUTULDU", Icons.Default.Error)
        "DEVAM_EDIYOR" -> Triple(Color(0xFFEF6C00), "Etkinlik Devam Ediyor", Icons.Default.AccessTime)
        else -> Triple(Color(0xFF1565C0), "Gelecek Etkinlik", Icons.Default.CalendarToday)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("event_card_${event.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }

                if (isAssignedToCurrentUser) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Atandınız",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = event.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatDateTr(event.startDate)} - ${formatDateTr(event.endDate)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (assignedCount > 0) "👥 $assignedCount Görevli Sorumlu" else "👤 Sorumlu Atanmamış",
                    fontSize = 11.sp,
                    color = if (assignedCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun MediaItemRow(
    mediaItem: MediaItemEntity,
    isAdmin: Boolean,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Document / Photo Icon Box
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (mediaItem.isDocument) MaterialTheme.colorScheme.tertiaryContainer
                        else MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (mediaItem.isDocument) Icons.Default.Description else Icons.Default.Image,
                    contentDescription = null,
                    tint = if (mediaItem.isDocument) MaterialTheme.colorScheme.onTertiaryContainer
                           else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mediaItem.fileName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Gönderen: ${mediaItem.uploadedByName}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Tarih: ${formatDateTr(mediaItem.uploadTimestamp)}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }

            if (isAdmin) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Belgeyi Sil",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    activeTab: String,
    onTabSelect: (String) -> Unit,
    isAdmin: Boolean
) {
    NavigationBar {
        NavigationBarItem(
            selected = activeTab == "dashboard",
            onClick = { onTabSelect("dashboard") },
            icon = { Icon(Icons.Default.Dashboard, "Kontrol Paneli") },
            label = { Text("Panel", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = activeTab == "events",
            onClick = { onTabSelect("events") },
            icon = { Icon(Icons.Default.Event, "Etkinlikler") },
            label = { Text("Etkinlikler", fontSize = 11.sp) }
        )
        if (isAdmin) {
            NavigationBarItem(
                selected = activeTab == "users",
                onClick = { onTabSelect("users") },
                icon = { Icon(Icons.Default.AdminPanelSettings, "Yönetim") },
                label = { Text("Öğretmenler", fontSize = 11.sp) }
            )
        }
    }
}

// Dialog: Simulate Date
@Composable
fun DateSimulationDialog(
    currentSimDate: Long,
    onClose: () -> Unit,
    onUpdateDate: (Int) -> Unit,
    onResetDate: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Tarih Simülasyonu", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Etkinliklerin süre dolumunu ve bildirimlerin zamanında çalışıp çalışmadığını test etmek için uygulama tarihini simüle edebilirsiniz.",
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Şu Anki Simüle Edilen Gün:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatDateTr(currentSimDate),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = { onUpdateDate(-12) },
                    modifier = Modifier.fillMaxWidth().height(36.dp).padding(vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Text("12 Gün Geriye Git (Geçmiş Testi)", fontSize = 12.sp)
                }

                Button(
                    onClick = { onUpdateDate(4) },
                    modifier = Modifier.fillMaxWidth().height(36.dp).padding(vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Text("4 Gün İleriye Git (Aktiflik Testi)", fontSize = 12.sp)
                }

                Button(
                    onClick = { onUpdateDate(15) },
                    modifier = Modifier.fillMaxWidth().height(36.dp).padding(vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Text("15 Gün İleriye Git (Eksik Belge Uyarısı)", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onResetDate,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sıfırla (Bugün)", fontSize = 11.sp)
                    }
                    Button(
                        onClick = onClose,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Kapat", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// Dialog: Upload Simulated File
@Composable
fun UploadSimulatedFileDialog(
    eventTitle: String,
    onClose: () -> Unit,
    onUpload: (String, Boolean, String) -> Unit
) {
    var fileName by remember { mutableStateOf("") }
    var isDocument by remember { mutableStateOf(false) }
    
    // Built-in beautiful template files to make simulation visually stunning and easy!
    val visualTemplates = remember(isDocument) {
        if (isDocument) {
            listOf(
                "etkinlik_raporu_imzali.pdf",
                "katilimci_listesi.xlsx",
                "resmi_izin_belgesi.docx",
                "okul_web_site_metni.txt"
            )
        } else {
            listOf(
                "okul_bahcesi_toplu_foto.jpg",
                "kupa_toreni_odul_ani.jpg",
                "proje_standlari_sergisi.png",
                "ogretmenler_kurulu_hatirasi.jpg"
            )
        }
    }

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Görsel / Belge Yükle", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "'$eventTitle' etkinliği için okul web sitesine gönderilecek dosyayı simüle edin.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Dosya Türü Seçin", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !isDocument,
                        onClick = { isDocument = false; fileName = "" },
                        label = { Text("Görsel (Fotoğraf)") }
                    )
                    FilterChip(
                        selected = isDocument,
                        onClick = { isDocument = true; fileName = "" },
                        label = { Text("Belge (PDF, Word...)") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Simüle Hazır Dosyalar (Seçmek için tıklayın):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    visualTemplates.forEach { temp ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { fileName = temp }
                                .background(
                                    if (fileName == temp) MaterialTheme.colorScheme.primaryContainer
                                    else Color.Transparent,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isDocument) Icons.Default.Description else Icons.Default.Image,
                                contentDescription = null,
                                tint = if (fileName == temp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(temp, fontSize = 12.sp, fontWeight = if (fileName == temp) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Dosya Adı veya Başlığı") },
                    placeholder = { Text("ornek_foto.jpg") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("upload_file_name_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onClose,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("İptal")
                    }
                    Button(
                        onClick = {
                            if (fileName.isNotEmpty()) {
                                onUpload(fileName, isDocument, if (isDocument) "sim_doc" else "sim_img")
                            }
                        },
                        modifier = Modifier.weight(1f).testTag("dialog_upload_submit"),
                        enabled = fileName.isNotEmpty()
                    ) {
                        Text("Yükle")
                    }
                }
            }
        }
    }
}

// Dialog: Add Authorized User
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserDialog(
    onClose: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("TEACHER") } // TEACHER or ADMIN

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Öğretmen Yetkilendir", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Gmail Adresi") },
                    placeholder = { Text("ornek@gmail.com") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().testTag("add_user_email_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Adı Soyadı (Branşı)") },
                    placeholder = { Text("Örn: Fatma Demir (Matematik)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_user_name_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Rol Seçin", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = role == "TEACHER",
                        onClick = { role = "TEACHER" },
                        label = { Text("Öğretmen") }
                    )
                    FilterChip(
                        selected = role == "ADMIN",
                        onClick = { role = "ADMIN" },
                        label = { Text("Yönetici") }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onClose,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("İptal")
                    }
                    Button(
                        onClick = {
                            if (email.isNotEmpty() && name.isNotEmpty()) {
                                onAdd(email, name, role)
                            }
                        },
                        modifier = Modifier.weight(1f).testTag("dialog_add_user_submit"),
                        enabled = email.isNotEmpty() && name.isNotEmpty()
                    ) {
                        Text("Yetkilendir")
                    }
                }
            }
        }
    }
}
