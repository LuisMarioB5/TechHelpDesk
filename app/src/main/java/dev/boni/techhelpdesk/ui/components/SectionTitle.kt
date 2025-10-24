package dev.boni.techhelpdesk.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

/**
 * Un componente de texto estandarizado para los títulos de las secciones.
 *
 * @param text El texto del título a mostrar.
 * @param modifier Un modificador opcional.
 */
@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 24.sp
    )
}

@Preview(showBackground = true)
@Composable
fun SectionTitlePreview() {
    TechHelpDeskTheme {
        SectionTitle(text = "Resumen de tickets")
    }
}
