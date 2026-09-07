package com.multipoisson.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multipoisson.app.domain.SchoolYear
import com.multipoisson.app.navigation.Screen
import com.multipoisson.app.ui.theme.AppColors
import com.multipoisson.app.ui.viewmodel.CreateProfileViewModel

// ── Step metadata ──────────────────────────────────────────────────────────────

private val STEPS = listOf(
    Triple("🐟", "Comment tu t'appelles ?",       "Écris ton prénom"),
    Triple("📚", "Tu es en quelle classe ?",        "Choisis ta classe"),
    Triple("🎂", "C'est quand ton anniversaire ?",  "Pour les événements spéciaux"),
)

// ── Screen ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(navController: NavController) {
    val vm: CreateProfileViewModel = viewModel()
    var step by remember { mutableIntStateOf(0) }
    var goingForward by remember { mutableStateOf(true) }
    var nameError by remember { mutableStateOf(false) }

    // Navigate back when profile is created
    LaunchedEffect(vm.done) {
        if (vm.done) navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        if (step == 0) navController.popBackStack()
                        else { goingForward = false; step-- }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background),
            )
        },
        containerColor = AppColors.Background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
        ) {
            // ── Progress dots ──────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                STEPS.indices.forEach { i ->
                    val active = i == step
                    val done   = i < step
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (active) 10.dp else 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                when {
                                    active -> AppColors.Blue
                                    done   -> AppColors.Green
                                    else   -> AppColors.Border
                                }
                            ),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Animated step content ──────────────────────────────────────
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    val forward = goingForward
                    (slideInHorizontally(tween(280)) { if (forward) it else -it } + fadeIn(tween(220))) togetherWith
                    (slideOutHorizontally(tween(280)) { if (forward) -it else it } + fadeOut(tween(180)))
                },
                label = "step",
                modifier = Modifier.weight(1f),
            ) { currentStep ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val (emoji, title, subtitle) = STEPS[currentStep]

                    Text(emoji, fontSize = 64.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(title, fontSize = 24.sp, fontWeight = FontWeight.Black, color = AppColors.TextPrimary, textAlign = TextAlign.Center)
                    Text(subtitle, fontSize = 14.sp, color = AppColors.TextSecondary, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(32.dp))

                    when (currentStep) {
                        0 -> StepName(vm = vm, hasError = nameError, onErrorChange = { nameError = it })
                        1 -> StepClass(vm = vm)
                        2 -> StepBirthdate(vm = vm)
                    }
                }
            }

            // ── Next / Finish button ───────────────────────────────────────
            val isLast = step == STEPS.lastIndex
            Button(
                onClick = {
                    when (step) {
                        0 -> {
                            if (vm.name.trim().length < 2) { nameError = true; return@Button }
                            nameError = false; goingForward = true; step++
                        }
                        1 -> { goingForward = true; step++ }
                        2 -> vm.create()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isLast) AppColors.Green else AppColors.Blue),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                enabled = !vm.isSaving,
            ) {
                if (vm.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = AppColors.White, strokeWidth = 2.5.dp)
                } else if (isLast) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = AppColors.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Créer mon profil !", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AppColors.White)
                } else {
                    Text("Suivant", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AppColors.White)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = AppColors.White)
                }
            }
        }
    }
}

// ── Step 1 — Name ──────────────────────────────────────────────────────────────

@Composable
private fun StepName(vm: CreateProfileViewModel, hasError: Boolean, onErrorChange: (Boolean) -> Unit) {
    OutlinedTextField(
        value = vm.name,
        onValueChange = { if (it.length <= 20) { vm.name = it; onErrorChange(false) } },
        placeholder = { Text("Prénom...", color = AppColors.TextSecondary) },
        singleLine = true,
        isError = hasError,
        supportingText = if (hasError) ({ Text("Le prénom doit faire au moins 2 caractères", color = AppColors.Red) }) else null,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Words,
            imeAction = ImeAction.Next,
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.Blue,
            unfocusedBorderColor = AppColors.Border,
        ),
        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary),
    )

    if (vm.name.isNotBlank()) {
        Spacer(Modifier.height(24.dp))
        Surface(shape = RoundedCornerShape(20.dp), color = AppColors.BlueLight, shadowElevation = 0.dp) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("🐟", fontSize = 32.sp)
                Column {
                    Text("Bonjour !", fontSize = 12.sp, color = AppColors.Blue)
                    Text(vm.name.trim(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = AppColors.BlueDark)
                }
            }
        }
    }
}

// ── Step 2 — School year ───────────────────────────────────────────────────────

private val DISPLAYED_YEARS = listOf(
    SchoolYear.CP, SchoolYear.CE1, SchoolYear.CE2,
    SchoolYear.CM1, SchoolYear.CM2,
    SchoolYear.SIXIEME, SchoolYear.CINQUIEME,
    SchoolYear.QUATRIEME, SchoolYear.TROISIEME,
    SchoolYear.LYCEE_PLUS,
)

@Composable
private fun StepClass(vm: CreateProfileViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        // 2-column grid of chips
        val rows = DISPLAYED_YEARS.chunked(2)
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { sy ->
                    val selected = vm.schoolYear == sy
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) AppColors.Blue else AppColors.White)
                            .border(
                                width = if (selected) 0.dp else 1.5.dp,
                                color = if (selected) Color.Transparent else AppColors.Border,
                                shape = RoundedCornerShape(14.dp),
                            )
                            .clickable { vm.schoolYear = sy },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            sy.label,
                            fontSize = 16.sp,
                            fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold,
                            color = if (selected) AppColors.White else AppColors.TextPrimary,
                        )
                    }
                }
                // Fill last row if odd count
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

// ── Step 3 — Birthdate ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepBirthdate(vm: CreateProfileViewModel) {
    val months = listOf("Janvier","Février","Mars","Avril","Mai","Juin","Juillet","Août","Septembre","Octobre","Novembre","Décembre")
    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    val years = (currentYear - 3 downTo currentYear - 20).toList()

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Day
        BirthdateDropdown(
            label = "Jour",
            options = (1..31).map { it.toString() },
            selectedIndex = vm.birthDay - 1,
            onSelect = { vm.birthDay = it + 1 },
            modifier = Modifier.weight(1f),
        )
        // Month
        BirthdateDropdown(
            label = "Mois",
            options = months,
            selectedIndex = vm.birthMonth - 1,
            onSelect = { vm.birthMonth = it + 1 },
            modifier = Modifier.weight(2f),
        )
        // Year
        BirthdateDropdown(
            label = "Année",
            options = years.map { it.toString() },
            selectedIndex = years.indexOf(vm.birthYear).coerceAtLeast(0),
            onSelect = { vm.birthYear = years[it] },
            modifier = Modifier.weight(1.5f),
        )
    }

    Spacer(Modifier.height(16.dp))
    Text(
        "🎂 Ton anniversaire sera fêté dans l'appli !",
        fontSize = 13.sp,
        color = AppColors.TextSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthdateDropdown(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val safeIndex = selectedIndex.coerceIn(0, options.lastIndex)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = options[safeIndex],
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 11.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Blue,
                unfocusedBorderColor = AppColors.Border,
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 220.dp),
        ) {
            options.forEachIndexed { i, opt ->
                DropdownMenuItem(
                    text = { Text(opt, fontWeight = if (i == safeIndex) FontWeight.Bold else FontWeight.Normal) },
                    onClick = { onSelect(i); expanded = false },
                    colors = MenuDefaults.itemColors(
                        textColor = if (i == safeIndex) AppColors.Blue else AppColors.TextPrimary,
                    ),
                )
            }
        }
    }
}
