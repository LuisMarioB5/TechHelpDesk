// MobileButton.kt (Componente Reutilizable)

package dev.boni.techhelpdesk.ui.components // Asegúrate que el paquete sea el correcto

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

// Enums para Variante y Tamaño (como en el código React)
enum class MobileButtonVariant {
    FILLED, OUTLINED, TEXT, ELEVATED
}

enum class MobileButtonSize {
    SMALL, MEDIUM, LARGE
}

/**
 * Botón personalizable que replica las variantes y tamaños de tu app web.
 * Incluye efecto de escala al pulsar.
 *
 * @param onClick Acción al pulsar.
 * @param modifier Modificador de Compose.
 * @param variant Estilo del botón (FILLED, OUTLINED, TEXT, ELEVATED). Por defecto FILLED.
 * @param size Tamaño del botón (SMALL, MEDIUM, LARGE). Por defecto MEDIUM.
 * @param fullWidth Si debe ocupar todo el ancho disponible. Por defecto false.
 * @param enabled Si el botón está activo. Por defecto true.
 * @param content El contenido del botón (ej. Texto o Icono).
 */
@Composable
fun MobileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: MobileButtonVariant = MobileButtonVariant.FILLED,
    size: MobileButtonSize = MobileButtonSize.MEDIUM,
    fullWidth: Boolean = false,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    // Efecto `active:scale-95`
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "scale")

    // Mapeo de tamaños (React -> Compose)
    // Ajustamos padding y estilo de texto según el tamaño
    val (contentPadding, textStyle) = when (size) {
        MobileButtonSize.SMALL -> PaddingValues(horizontal = 16.dp, vertical = 9.dp) to MaterialTheme.typography.labelMedium // h-10, text-sm
        MobileButtonSize.MEDIUM -> PaddingValues(horizontal = 24.dp, vertical = 12.dp) to MaterialTheme.typography.labelLarge // h-12, text-base
        MobileButtonSize.LARGE -> PaddingValues(horizontal = 32.dp, vertical = 16.dp) to MaterialTheme.typography.titleMedium // h-14, text-lg
    }

    // Combinamos modificadores: el que viene de fuera, el fullWidth y la escala
    val finalModifier = modifier
        .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }

    // Usamos CompositionLocalProvider para aplicar el estilo de texto correcto al contenido
    CompositionLocalProvider(LocalTextStyle provides textStyle) {
        when (variant) {
            MobileButtonVariant.FILLED -> Button(
                onClick = onClick,
                modifier = finalModifier,
                enabled = enabled,
                shape = RoundedCornerShape(12.dp), // rounded-xl
                colors = ButtonDefaults.buttonColors( // Estilo Filled por defecto
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = contentPadding,
                interactionSource = interactionSource,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp, pressedElevation = 1.dp), // Sombra sutil
                content = content
            )
            MobileButtonVariant.OUTLINED -> OutlinedButton(
                onClick = onClick,
                modifier = finalModifier,
                enabled = enabled,
                shape = RoundedCornerShape(12.dp), // rounded-xl
                colors = ButtonDefaults.outlinedButtonColors( // Estilo Outlined
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary), // Borde más fino que en React
                contentPadding = contentPadding,
                interactionSource = interactionSource,
                content = content
            )
            MobileButtonVariant.TEXT -> TextButton(
                onClick = onClick,
                modifier = finalModifier,
                enabled = enabled,
                shape = RoundedCornerShape(12.dp), // rounded-xl
                colors = ButtonDefaults.textButtonColors( // Estilo Text
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = contentPadding,
                interactionSource = interactionSource,
                content = content
            )
            MobileButtonVariant.ELEVATED -> ElevatedButton(
                onClick = onClick,
                modifier = finalModifier,
                enabled = enabled,
                shape = RoundedCornerShape(12.dp), // rounded-xl
                colors = ButtonDefaults.elevatedButtonColors( // Estilo Elevated
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 3.dp, pressedElevation = 6.dp), // Sombra
                contentPadding = contentPadding,
                interactionSource = interactionSource,
                content = content
            )
        }
    }
}