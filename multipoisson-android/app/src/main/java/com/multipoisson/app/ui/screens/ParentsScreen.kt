package com.multipoisson.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multipoisson.app.navigation.Screen
import com.multipoisson.app.ui.theme.AppColors
import com.multipoisson.app.ui.viewmodel.ParentsViewModel

// ── Profile slot colors (same cycle as ProfilePicker) ─────────────────────────
private val SLOT_COLORS = listOf(
    AppColors.Blue,
    AppColors.Green,
    AppColors.Orange,
    AppColors.Purple,
    Color(0xFFE91E8C),
)

// ── Root ───────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentsScreen(navController: NavController) {
    val vm: ParentsViewModel = viewModel()
    val isUnlocked = vm.isUnlocked
    val codeHash by vm.parentalCodeHash.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Espace Parents", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background),
            )
        },
        containerColor = AppColors.Background,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (!isUnlocked) {
                PinGate(
                    hasCode = codeHash.isNotEmpty(),
                    onValidate = vm::validateAndUnlock,
                    onCreate = vm::createCode,
                )
            } else {
                ParentsContent(
                    vm = vm,
                    navController = navController,
                )
            }
        }
    }
}

// ── PIN gate ───────────────────────────────────────────────────────────────────

private enum class PinStep { ENTER, CREATE_NEW, CREATE_CONFIRM }

@Composable
private fun PinGate(
    hasCode: Boolean,
    onValidate: (String) -> Boolean,
    onCreate: (String) -> Unit,
) {
    var step by remember { mutableStateOf(if (hasCode) PinStep.ENTER else PinStep.CREATE_NEW) }
    var pin by remember { mutableStateOf("") }
    var firstPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    // Auto-submit when 4 digits reached
    LaunchedEffect(pin.length) {
        if (pin.length < 4) return@LaunchedEffect
        when (step) {
            PinStep.ENTER -> {
                if (!onValidate(pin)) {
                    error = "Code incorrect"
                    pin = ""
                }
            }
            PinStep.CREATE_NEW -> {
                firstPin = pin
                pin = ""
                step = PinStep.CREATE_CONFIRM
                error = null
            }
            PinStep.CREATE_CONFIRM -> {
                if (pin == firstPin) {
                    onCreate(pin)
                } else {
                    error = "Les codes ne correspondent pas"
                    pin = ""
                    step = PinStep.CREATE_NEW
                    firstPin = ""
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("🔐", fontSize = 56.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            when (step) {
                PinStep.ENTER          -> "Code parental"
                PinStep.CREATE_NEW     -> "Créer un code"
                PinStep.CREATE_CONFIRM -> "Confirmer le code"
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = AppColors.TextPrimary,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            when (step) {
                PinStep.ENTER          -> "Entrez votre code à 4 chiffres"
                PinStep.CREATE_NEW     -> "Choisissez un code à 4 chiffres"
                PinStep.CREATE_CONFIRM -> "Saisissez-le à nouveau"
            },
            fontSize = 14.sp,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(32.dp))

        // 4 dots
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { i ->
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (i < pin.length) AppColors.Blue else AppColors.Border),
                )
            }
        }

        // Error message
        if (error != null) {
            Spacer(Modifier.height(10.dp))
            Text(error!!, fontSize = 13.sp, color = AppColors.Red, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(32.dp))

        // Custom numpad
        PinNumpad(
            onDigit = { d -> if (pin.length < 4) { pin += d; error = null } },
            onDelete = { if (pin.isNotEmpty()) pin = pin.dropLast(1) },
        )
    }
}

@Composable
private fun PinNumpad(onDigit: (String) -> Unit, onDelete: () -> Unit) {
    val rows = listOf(listOf("1","2","3"), listOf("4","5","6"), listOf("7","8","9"), listOf("","0","⌫"))
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { key ->
                    if (key.isEmpty()) {
                        Spacer(Modifier.size(72.dp))
                    } else {
                        OutlinedButton(
                            onClick = { if (key == "⌫") onDelete() else onDigit(key) },
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.TextPrimary),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(AppColors.Border),
                            ),
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Text(key, fontSize = if (key == "⌫") 20.sp else 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ── Main content ───────────────────────────────────────────────────────────────

@Composable
private fun ParentsContent(vm: ParentsViewModel, navController: NavController) {
    val profiles by vm.profiles.collectAsState()
    val activeProfileId by vm.activeProfileId.collectAsState()
    val soundEnabled by vm.soundEnabled.collectAsState()

    var deleteTargetId by remember { mutableStateOf<String?>(null) }
    var showChangeCode by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // ── Profils ────────────────────────────────────────────────────────
        item {
            SectionHeader(icon = Icons.Filled.People, title = "Profils  (${profiles.size}/5)")
        }
        item {
            Surface(shape = RoundedCornerShape(16.dp), color = AppColors.White, shadowElevation = 2.dp) {
                Column {
                    profiles.forEachIndexed { index, profile ->
                        val isActive = profile.id == activeProfileId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            // Avatar circle
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SLOT_COLORS[index % SLOT_COLORS.size])
                                    .then(
                                        if (isActive) Modifier.border(2.dp, AppColors.Blue, CircleShape)
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("🐟", fontSize = 18.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(profile.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
                                if (isActive) Text("Profil actif", fontSize = 12.sp, color = AppColors.Blue, fontWeight = FontWeight.SemiBold)
                            }
                            IconButton(onClick = { deleteTargetId = profile.id }) {
                                Icon(Icons.Filled.DeleteOutline, contentDescription = "Supprimer", tint = AppColors.Red.copy(alpha = 0.7f))
                            }
                        }
                        if (index < profiles.lastIndex) HorizontalDivider(color = AppColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                    if (profiles.size < 5) {
                        HorizontalDivider(color = AppColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
                        TextButton(
                            onClick = { navController.navigate(Screen.CreateProfile.route) },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        ) {
                            Icon(Icons.Filled.PersonAdd, contentDescription = null, tint = AppColors.Blue, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Ajouter un profil", color = AppColors.Blue, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // ── Son ────────────────────────────────────────────────────────────
        item { SectionHeader(icon = Icons.Filled.VolumeUp, title = "Son") }
        item {
            SettingCard {
                SettingRow(
                    label = "Sons activés",
                    trailing = {
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = vm::setSoundEnabled,
                            colors = SwitchDefaults.colors(checkedThumbColor = AppColors.White, checkedTrackColor = AppColors.Blue),
                        )
                    },
                )
            }
        }

        // ── Sécurité ───────────────────────────────────────────────────────
        item { SectionHeader(icon = Icons.Filled.Lock, title = "Sécurité") }
        item {
            SettingCard {
                SettingRow(
                    label = "Modifier le code parental",
                    trailing = {
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = AppColors.TextSecondary)
                    },
                    onClick = { showChangeCode = true },
                )
                HorizontalDivider(color = AppColors.Border)
                SettingRow(
                    label = "Changer de profil",
                    labelColor = AppColors.Orange,
                    trailing = {
                        Icon(Icons.Filled.SwitchAccount, contentDescription = null, tint = AppColors.Orange)
                    },
                    onClick = {
                        vm.signOut()
                        navController.navigate(Screen.ProfilePicker.route) {
                            popUpTo(Screen.Main.route) { inclusive = true }
                        }
                    },
                )
            }
        }

        // ── Premium ────────────────────────────────────────────────────────
        item { SectionHeader(icon = Icons.Filled.Star, title = "Premium") }
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AppColors.Yellow.copy(alpha = 0.15f),
                shadowElevation = 0.dp,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("⭐", fontSize = 36.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Débloquer MultiPoisson+", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
                        Text("Tous les poissons, 5 profils, pas de pub", fontSize = 12.sp, color = AppColors.TextSecondary)
                    }
                    Button(
                        onClick = { /* TODO: Play Billing */ },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Orange),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text("4,99 €", fontSize = 13.sp, fontWeight = FontWeight.Black, color = AppColors.White)
                    }
                }
            }
        }

        // ── Légal ──────────────────────────────────────────────────────────
        item { SectionHeader(icon = Icons.Filled.Gavel, title = "Légal") }
        item {
            SettingCard {
                SettingRow(label = "Mentions légales", trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = AppColors.TextSecondary) })
                HorizontalDivider(color = AppColors.Border)
                SettingRow(label = "Politique de confidentialité", trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = AppColors.TextSecondary) })
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }

    // ── Delete dialog ──────────────────────────────────────────────────────
    if (deleteTargetId != null) {
        val name = profiles.find { it.id == deleteTargetId }?.name ?: ""
        AlertDialog(
            onDismissRequest = { deleteTargetId = null },
            title = { Text("Supprimer $name ?") },
            text = { Text("Tout l'historique de jeu sera supprimé définitivement.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteProfile(deleteTargetId!!)
                    if (deleteTargetId == activeProfileId) {
                        navController.navigate(Screen.ProfilePicker.route) {
                            popUpTo(Screen.Main.route) { inclusive = true }
                        }
                    }
                    deleteTargetId = null
                }) {
                    Text("Supprimer", color = AppColors.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTargetId = null }) { Text("Annuler") }
            },
        )
    }

    // ── Change code dialog ─────────────────────────────────────────────────
    if (showChangeCode) {
        ChangeCodeDialog(
            onConfirm = { old, new -> vm.changeCode(old, new) },
            onDismiss = { showChangeCode = false },
        )
    }
}

// ── Change code dialog ─────────────────────────────────────────────────────────

@Composable
private fun ChangeCodeDialog(onConfirm: (String, String) -> Boolean, onDismiss: () -> Unit) {
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var step by remember { mutableIntStateOf(0) } // 0=old, 1=new
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier le code") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(if (step == 0) "Code actuel" else "Nouveau code (4 chiffres)", fontSize = 14.sp, color = AppColors.TextSecondary)
                OutlinedTextField(
                    value = if (step == 0) oldPin else newPin,
                    onValueChange = { v ->
                        val digits = v.filter { it.isDigit() }.take(4)
                        if (step == 0) oldPin = digits else newPin = digits
                        error = null
                    },
                    placeholder = { Text("• • • •") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword,
                    ),
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    isError = error != null,
                    supportingText = error?.let { { Text(it, color = AppColors.Red) } },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (step == 0) {
                    if (oldPin.length == 4) step = 1
                    else error = "4 chiffres requis"
                } else {
                    if (newPin.length < 4) { error = "4 chiffres requis"; return@TextButton }
                    if (onConfirm(oldPin, newPin)) onDismiss()
                    else { error = "Code actuel incorrect"; step = 0; oldPin = ""; newPin = "" }
                }
            }) {
                Text(if (step == 0) "Suivant" else "Confirmer", fontWeight = FontWeight.Bold, color = AppColors.Blue)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } },
    )
}

// ── Reusable sub-composables ───────────────────────────────────────────────────

@Composable
private fun SectionHeader(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(start = 4.dp, top = 4.dp),
    ) {
        Icon(icon, contentDescription = null, tint = AppColors.TextSecondary, modifier = Modifier.size(16.dp))
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.TextSecondary)
    }
}

@Composable
private fun SettingCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = AppColors.White, shadowElevation = 2.dp) {
        Column(content = content)
    }
}

@Composable
private fun SettingRow(
    label: String,
    labelColor: Color = AppColors.TextPrimary,
    trailing: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
) {
    val rowModifier = onClick
        ?.let { Modifier.fillMaxWidth().clickable(onClick = it) }
        ?: Modifier.fillMaxWidth()
    Row(
        modifier = rowModifier.padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 15.sp, color = labelColor, modifier = Modifier.weight(1f))
        trailing()
    }
}
