package ru.forvid.o2devsstud.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus

@Composable
fun OrderItem(
    order: Order,
    onChangeStatus: (Long, OrderStatus) -> Unit,
    onPickDocuments: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "${order.from} → ${order.to}")
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Заявка: ${order.requestNumber} · Статус: ${order.status}")
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Расчетное время: ${order.estimatedDays} дн.")

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons — пример набора
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Взял в работу
                Button(onClick = { onChangeStatus(order.id, OrderStatus.TAKEN) }) {
                    Text("Взял в работу")
                }
                Button(onClick = { onChangeStatus(order.id, OrderStatus.IN_TRANSIT_TO_PICKUP) }) {
                    Text("В дороге к месту погрузки")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onChangeStatus(order.id, OrderStatus.LOADED) }) {
                    Text("Машина загружена")
                }
                Button(onClick = { onChangeStatus(order.id, OrderStatus.IN_TRANSIT_TO_DROPOFF) }) {
                    Text("В дороге на место разгрузки")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onChangeStatus(order.id, OrderStatus.PARKED) }) {
                    Text("На стоянке")
                }
                Button(onClick = { onChangeStatus(order.id, OrderStatus.ARRIVED_DROPOFF) }) {
                    Text("ТС прибыло на место разгрузки")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onChangeStatus(order.id, OrderStatus.UNLOADED) }) {
                    Text("Машина разгружена")
                }
                Button(onClick = { onPickDocuments(order.id) }) {
                    Text("Забрал документы")
                }
            }
        }
    }
}
