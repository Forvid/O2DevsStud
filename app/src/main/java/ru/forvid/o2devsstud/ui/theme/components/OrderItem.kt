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
    onShowMap: (Long) -> Unit,
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onChangeStatus(order.id, OrderStatus.IN_WORK) }) {
                    Text("Взял в работу")
                }
                Button(onClick = { onChangeStatus(order.id, OrderStatus.ON_WAY_TO_LOAD) }) {
                    Text("В дороге к месту погрузки")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onChangeStatus(order.id, OrderStatus.LOADED) }) {
                    Text("Машина загружена")
                }
                Button(onClick = { onChangeStatus(order.id, OrderStatus.ON_WAY_TO_UNLOAD) }) {
                    Text("В дороге на место разгрузки")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onChangeStatus(order.id, OrderStatus.PARKED) }) {
                    Text("На стоянке")
                }
                Button(onClick = { onChangeStatus(order.id, OrderStatus.ARRIVED_UNLOAD) }) {
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

            Spacer(modifier = Modifier.height(8.dp))

            // Показать на карте (если трек есть/будет по id)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = { onShowMap(order.id) }) {
                    Text("Показать на карте")
                }
            }
        }
    }
}
