package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class PatternKind { GINGHAM, DOTS, STRIPES, KRAFT, GRID }
enum class PaperStyle { LINED_CREAM, LINED_BLUE, LINED_PINK, GRID, PLAIN, KRAFT }

@Immutable
data class CottonsColors(
    val name: String,
    val background: Color,
    val paper: Color,
    val paperAlt: Color,
    val ink: Color,
    val inkSoft: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val checkA: Color,
    val checkB: Color,
    val tape: Color,
    val seal: Color,
    val shadow: Color,
    val pattern: PatternKind,
    val isDark: Boolean = false
) {
    fun toDark(): CottonsColors = CottonsColors(
        name = "$name (Dark)",
        background = Color(0xFF191617),
        paper = Color(0xFF262122),
        paperAlt = Color(0xFF332B2D),
        ink = Color(0xFFF7EBEB),
        inkSoft = Color(0xFFA6999B),
        primary = this.primary,
        secondary = this.secondary,
        tertiary = this.tertiary,
        checkA = Color(0xFF302729),
        checkB = Color(0xFF191617),
        tape = this.tape,
        seal = this.seal,
        shadow = Color(0x66000000),
        pattern = this.pattern,
        isDark = true
    )
}

object CottonsThemes {
    val PicnicRed = CottonsColors(
        name = "Picnic Red",
        background = Color(0xFFFFF6EE),
        paper = Color(0xFFFFFBF2),
        paperAlt = Color(0xFFF7C6CB),
        ink = Color(0xFF3B1A1F),
        inkSoft = Color(0xFF7A5A5F),
        primary = Color(0xFF9E1B2F),
        secondary = Color(0xFFC8283C),
        tertiary = Color(0xFFE9A3AC),
        checkA = Color(0xFFE56B7A),
        checkB = Color(0xFFFFF6EE),
        tape = Color(0xCCF5DD9C),
        seal = Color(0xFF9E1B2F),
        shadow = Color(0x223B1A1F),
        pattern = PatternKind.GINGHAM
    )

    val StrawberryMilk = CottonsColors(
        name = "Strawberry Milk",
        background = Color(0xFFFFF0F3),
        paper = Color(0xFFFFFBFD),
        paperAlt = Color(0xFFFFD6E0),
        ink = Color(0xFF4A1E29),
        inkSoft = Color(0xFF8A5A66),
        primary = Color(0xFFE04568),
        secondary = Color(0xFFFF758F),
        tertiary = Color(0xFFFFB3C1),
        checkA = Color(0xFFFF8FA3),
        checkB = Color(0xFFFFF0F3),
        tape = Color(0xCCFFB3C1),
        seal = Color(0xFFC9184A),
        shadow = Color(0x224A1E29),
        pattern = PatternKind.GINGHAM
    )

    val MatchaLatte = CottonsColors(
        name = "Matcha Latte",
        background = Color(0xFFF4F7EB),
        paper = Color(0xFFFAFCF5),
        paperAlt = Color(0xFFDCE6C5),
        ink = Color(0xFF263319),
        inkSoft = Color(0xFF627354),
        primary = Color(0xFF4E6B34),
        secondary = Color(0xFF7F9E58),
        tertiary = Color(0xFFB5C99A),
        checkA = Color(0xFF97A97C),
        checkB = Color(0xFFF4F7EB),
        tape = Color(0xCCCFE1B9),
        seal = Color(0xFF3A5A40),
        shadow = Color(0x22263319),
        pattern = PatternKind.GINGHAM
    )

    val TeddyBrownie = CottonsColors(
        name = "Teddy Brownie",
        background = Color(0xFFFBF4E9),
        paper = Color(0xFFFFFDF8),
        paperAlt = Color(0xFFEBDBC6),
        ink = Color(0xFF3E2723),
        inkSoft = Color(0xFF795548),
        primary = Color(0xFF6D4C41),
        secondary = Color(0xFF8D6E63),
        tertiary = Color(0xFFD7CCC8),
        checkA = Color(0xFFBCAAA4),
        checkB = Color(0xFFFBF4E9),
        tape = Color(0xCCD7CCC8),
        seal = Color(0xFF5D4037),
        shadow = Color(0x223E2723),
        pattern = PatternKind.GINGHAM
    )

    val SakuraPetal = CottonsColors(
        name = "Sakura Petal",
        background = Color(0xFFFFF5F7),
        paper = Color(0xFFFFFDFC),
        paperAlt = Color(0xFFFED7E2),
        ink = Color(0xFF3D232A),
        inkSoft = Color(0xFF7E5661),
        primary = Color(0xFFB83280),
        secondary = Color(0xFFD53F8C),
        tertiary = Color(0xFFFBB6CE),
        checkA = Color(0xFFF687B3),
        checkB = Color(0xFFFFF5F7),
        tape = Color(0xCCFBB6CE),
        seal = Color(0xFF97266D),
        shadow = Color(0x223D232A),
        pattern = PatternKind.DOTS
    )

    val OceanBlvd = CottonsColors(
        name = "Ocean Blvd",
        background = Color(0xFFF4EFE3),
        paper = Color(0xFFFBF6EA),
        paperAlt = Color(0xFFB7CCE8),
        ink = Color(0xFF0F1B3D),
        inkSoft = Color(0xFF4A5A80),
        primary = Color(0xFF14285A),
        secondary = Color(0xFF5C7FB1),
        tertiary = Color(0xFFB7CCE8),
        checkA = Color(0xFF5C7FB1),
        checkB = Color(0xFFFBF6EA),
        tape = Color(0xCCD9A441),
        seal = Color(0xFF14285A),
        shadow = Color(0x220F1B3D),
        pattern = PatternKind.GINGHAM
    )

    val BlueberryJam = CottonsColors(
        name = "Blueberry Jam",
        background = Color(0xFFF0F4FC),
        paper = Color(0xFFFBFDFF),
        paperAlt = Color(0xFFD0DCF2),
        ink = Color(0xFF1A2645),
        inkSoft = Color(0xFF536387),
        primary = Color(0xFF2C4A8A),
        secondary = Color(0xFF4F70B5),
        tertiary = Color(0xFFADC2EB),
        checkA = Color(0xFF7B9AD6),
        checkB = Color(0xFFF0F4FC),
        tape = Color(0xCCADC2EB),
        seal = Color(0xFF1E3566),
        shadow = Color(0x221A2645),
        pattern = PatternKind.GINGHAM
    )

    val LolyPastel = CottonsColors(
        name = "Loly Pastel",
        background = Color(0xFFFDEEF2),
        paper = Color(0xFFFFFDF8),
        paperAlt = Color(0xFFBFE3D2),
        ink = Color(0xFF3A2C2C),
        inkSoft = Color(0xFF7B6A66),
        primary = Color(0xFFD6738F),
        secondary = Color(0xFFBFE3D2),
        tertiary = Color(0xFFF6EBC0),
        checkA = Color(0xFFF4C9D6),
        checkB = Color(0xFFFFF7EC),
        tape = Color(0xCCF4C9D6),
        seal = Color(0xFF8A5A3C),
        shadow = Color(0x223A2C2C),
        pattern = PatternKind.DOTS
    )

    val ButterEspresso = CottonsColors(
        name = "Butter & Espresso",
        background = Color(0xFFFFF8E3),
        paper = Color(0xFFFFFDF5),
        paperAlt = Color(0xFFF5DD9C),
        ink = Color(0xFF532620),
        inkSoft = Color(0xFF8B5A4C),
        primary = Color(0xFF532620),
        secondary = Color(0xFFB77B45),
        tertiary = Color(0xFFF5DD9C),
        checkA = Color(0xFFE0C47C),
        checkB = Color(0xFFFFF8E3),
        tape = Color(0xCCB77B45),
        seal = Color(0xFF532620),
        shadow = Color(0x25532620),
        pattern = PatternKind.KRAFT
    )

    val ForestStamp = CottonsColors(
        name = "Forest Stamp",
        background = Color(0xFFEFF3DA),
        paper = Color(0xFFF8F6E4),
        paperAlt = Color(0xFFDCE6B8),
        ink = Color(0xFF25361F),
        inkSoft = Color(0xFF5E7050),
        primary = Color(0xFF2F4A28),
        secondary = Color(0xFF6E8F3A),
        tertiary = Color(0xFFA9C75B),
        checkA = Color(0xFF7FA348),
        checkB = Color(0xFFEFF3DA),
        tape = Color(0xCCC9A57A),
        seal = Color(0xFF2F4A28),
        shadow = Color(0x2225361F),
        pattern = PatternKind.GINGHAM
    )

    val MidnightStudy = CottonsColors(
        name = "Midnight Study",
        background = Color(0xFF141A29),
        paper = Color(0xFF1E2638),
        paperAlt = Color(0xFF2C3852),
        ink = Color(0xFFF0F4FC),
        inkSoft = Color(0xFF9CAECF),
        primary = Color(0xFF6082C5),
        secondary = Color(0xFF8BA5DB),
        tertiary = Color(0xFFE6C35C),
        checkA = Color(0xFF25324D),
        checkB = Color(0xFF141A29),
        tape = Color(0xCCE6C35C),
        seal = Color(0xFFE6C35C),
        shadow = Color(0x55000000),
        pattern = PatternKind.GRID,
        isDark = true
    )

    val TinCase = CottonsColors(
        name = "Tin Case",
        background = Color(0xFFE2EAF5),
        paper = Color(0xFFFBFBF7),
        paperAlt = Color(0xFFF2C4CE),
        ink = Color(0xFF2B3550),
        inkSoft = Color(0xFF6C7794),
        primary = Color(0xFF385785),
        secondary = Color(0xFF9DB8DC),
        tertiary = Color(0xFFF2C4CE),
        checkA = Color(0xFF9DB8DC),
        checkB = Color(0xFFFBFBF7),
        tape = Color(0xCCF2C4CE),
        seal = Color(0xFF9E5A78),
        shadow = Color(0x222B3550),
        pattern = PatternKind.STRIPES
    )

    val all = listOf(
        PicnicRed,
        StrawberryMilk,
        MatchaLatte,
        TeddyBrownie,
        SakuraPetal,
        OceanBlvd,
        BlueberryJam,
        LolyPastel,
        ButterEspresso,
        ForestStamp,
        MidnightStudy,
        TinCase
    )

    val map = all.associateBy { it.name }
}

val LocalCottons = staticCompositionLocalOf { CottonsThemes.PicnicRed }
