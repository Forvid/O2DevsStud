package ru.forvid.o2devsstud.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.forvid.o2devsstud.domain.model.Order

@Composable
fun OrderItem(
    order: Order,
    onOpenOrder: (Long) -> Unit,
    onShowMap: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onOpenOrder(order.id) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // --- Верхняя строка: заголовок и номер заявки ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${order.from} → ${order.to}",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "№ ${order.requestNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))

            // --- Средняя строка: Статус и расчетное время ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Статус слева
                Text(
                    // Использую displayName для красивого отображения статуса.
                    text = "Статус: ${order.status.displayName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                // Расчетное время справа
                Text(
                    text = "Время: ${order.estimatedDays} дн.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            // --- Нижняя строка: кнопка "Показать на карте" и стрелка в детали ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    // Использую вашу логику: если есть trackId, беру его, иначе - id заказа.
                    onClick = { onShowMap(order.trackId ?: order.id) }
                ) {
                    Text("Показать на карте")
                }

                // IconButton для визуального акцента на возможности перехода.
                IconButton(onClick = { onOpenOrder(order.id) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Открыть детали")
                }
            }
        }
    }
}
