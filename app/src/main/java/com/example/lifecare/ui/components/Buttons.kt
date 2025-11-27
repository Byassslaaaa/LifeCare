package com.example.lifecare.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lifecare.ui.theme.HealthColors
import com.example.lifecare.ui.theme.HealthSpacing
import com.example.lifecare.ui.theme.HealthTypography

/**
 * PrimaryButton - Main action button with primary color
 * Use for primary actions like "Save", "Submit", "Continue"
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(HealthSpacing.buttonHeight),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = HealthColors.Primary,
            contentColor = HealthColors.TextOnPrimary
        ),
        shape = RoundedCornerShape(HealthSpacing.cornerRadiusMedium)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(HealthSpacing.iconSizeSmall)
            )
            Spacer(modifier = Modifier.width(HealthSpacing.xSmall))
        }
        Text(
            text = text,
            style = HealthTypography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * SecondaryButton - Secondary action button with outline
 * Use for secondary actions like "Cancel", "Skip", "Back"
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(HealthSpacing.buttonHeight),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = HealthColors.Primary
        ),
        border = BorderStroke(1.dp, HealthColors.Primary),
        shape = RoundedCornerShape(HealthSpacing.cornerRadiusMedium)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(HealthSpacing.iconSizeSmall)
            )
            Spacer(modifier = Modifier.width(HealthSpacing.xSmall))
        }
        Text(
            text = text,
            style = HealthTypography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * TextOnlyButton - Text button for tertiary actions
 * Use for less important actions or inline links
 */
@Composable
fun TextOnlyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = HealthColors.Primary
        )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(HealthSpacing.iconSizeSmall)
            )
            Spacer(modifier = Modifier.width(HealthSpacing.xxSmall))
        }
        Text(
            text = text,
            style = HealthTypography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * DangerButton - Destructive action button (red)
 * Use for destructive actions like "Delete", "Remove", "Clear All"
 */
@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(HealthSpacing.buttonHeight),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = HealthColors.Error,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(HealthSpacing.cornerRadiusMedium)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(HealthSpacing.iconSizeSmall)
            )
            Spacer(modifier = Modifier.width(HealthSpacing.xSmall))
        }
        Text(
            text = text,
            style = HealthTypography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * CompactButton - Smaller button for inline actions
 * Use in cards or lists where full-width buttons are too large
 */
@Composable
fun CompactButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    variant: CompactButtonVariant = CompactButtonVariant.PRIMARY
) {
    val colors = when (variant) {
        CompactButtonVariant.PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = HealthColors.Primary,
            contentColor = HealthColors.TextOnPrimary
        )
        CompactButtonVariant.SECONDARY -> ButtonDefaults.outlinedButtonColors(
            contentColor = HealthColors.Primary
        )
    }

    if (variant == CompactButtonVariant.SECONDARY) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(36.dp),
            enabled = enabled,
            colors = colors,
            border = BorderStroke(1.dp, HealthColors.Primary),
            shape = RoundedCornerShape(HealthSpacing.cornerRadiusSmall),
            contentPadding = PaddingValues(
                horizontal = HealthSpacing.small,
                vertical = HealthSpacing.xxSmall
            )
        ) {
            ButtonContent(icon, text)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.height(36.dp),
            enabled = enabled,
            colors = colors,
            shape = RoundedCornerShape(HealthSpacing.cornerRadiusSmall),
            contentPadding = PaddingValues(
                horizontal = HealthSpacing.small,
                vertical = HealthSpacing.xxSmall
            )
        ) {
            ButtonContent(icon, text)
        }
    }
}

@Composable
private fun ButtonContent(icon: ImageVector?, text: String) {
    if (icon != null) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
    }
    Text(
        text = text,
        style = HealthTypography.labelSmall,
        fontWeight = FontWeight.Medium
    )
}

enum class CompactButtonVariant {
    PRIMARY,
    SECONDARY
}
