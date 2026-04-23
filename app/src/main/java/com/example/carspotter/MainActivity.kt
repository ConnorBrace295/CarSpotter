package com.example.carspotter

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.carspotter.ui.theme.CarSpotterTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CarSpotterTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {

    val context = LocalContext.current
    val db = remember { DatabaseInstance.getDatabase(context) }
    val dao = remember { db.carDao() }

    val scope = rememberCoroutineScope()
    val cars by dao.getAllCars().collectAsState(initial = emptyList())

    var search by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var editingCar by remember { mutableStateOf<CarEntity?>(null) }

    fun isIncomplete(car: CarEntity): Boolean {
        return car.model.isBlank() ||
                car.colour.isBlank() ||
                car.year == 0
    }

    val filtered = cars
        .filter {
            it.plate.contains(search, true) ||
                    it.model.contains(search, true) ||
                    it.colour.contains(search, true) ||
                    it.year.toString().contains(search)
        }
        .sortedBy { if (isIncomplete(it)) 0 else 1 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "CarSpotter",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search cars...") }
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                editingCar = null
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Car")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(
                items = filtered,
                key = { it.plate }
            ) { car ->

                CarCard(
                    car = car,
                    isIncomplete = isIncomplete(car),
                    onDelete = {
                        scope.launch { dao.deleteCar(car) }
                    },
                    onEdit = {
                        editingCar = car
                        showDialog = true
                    }
                )
            }
        }
    }

    if (showDialog) {
        AddOrEditCarDialog(
            existing = editingCar,
            onSave = { car ->

                showDialog = false

                scope.launch {

                    val existing = dao.getCarByPlate(car.plate)

                    val isEditing = editingCar != null

                    if (existing != null && existing.id != editingCar?.id) {
                        Toast.makeText(context, "Plate already exists", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    if (isEditing) {
                        dao.updateCar(car.copy(id = editingCar!!.id))
                    } else {
                        dao.insertCar(car)
                    }

                    editingCar = null
                }
            },
            onCancel = {
                showDialog = false
                editingCar = null
            }
        )
    }
}

@Composable
fun CarCard(
    car: CarEntity,
    isIncomplete: Boolean,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = if (car.model.isBlank()) "Unknown Model" else car.model,
                style = MaterialTheme.typography.titleLarge,
                color = if (isIncomplete) Color.Yellow else Color.White
            )

            Spacer(Modifier.height(4.dp))

            Text("Plate: ${car.plate}", color = Color.LightGray)
            Text("Year: ${car.year} • Colour: ${car.colour}", color = Color.LightGray)

            if (isIncomplete) {
                Text("INCOMPLETE", color = Color.Yellow)
            }

            Spacer(Modifier.height(8.dp))

            Row {
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun AddOrEditCarDialog(
    existing: CarEntity?,
    onSave: (CarEntity) -> Unit,
    onCancel: () -> Unit
) {

    var plate by remember { mutableStateOf(existing?.plate ?: "") }
    var model by remember { mutableStateOf(existing?.model ?: "") }
    var year by remember { mutableStateOf(existing?.year?.toString() ?: "") }
    var colour by remember { mutableStateOf(existing?.colour ?: "") }

    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    CarEntity(
                        id = existing?.id ?: 0,
                        plate = plate,
                        model = model,
                        year = year.toIntOrNull() ?: 0,
                        colour = colour,
                        imageUrl = ""
                    )
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        },
        title = {
            Text(if (existing == null) "Add Car" else "Edit Car")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = plate,
                    onValueChange = { plate = it.uppercase() },
                    label = { Text("Plate") }
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = {
                        model = it.split(" ").joinToString(" ") { word ->
                            word.replaceFirstChar { c ->
                                if (c.isLowerCase()) c.titlecase() else c.toString()
                            }
                        }
                    },
                    label = { Text("Model") }
                )
                OutlinedTextField(year, { year = it }, label = { Text("Year") })
                OutlinedTextField(
                    value = colour,
                    onValueChange = {
                        colour = it.replaceFirstChar { c ->
                            if (c.isLowerCase()) c.titlecase() else c.toString()
                        }
                    },
                    label = { Text("Colour") }
                )
            }
        }
    )
}