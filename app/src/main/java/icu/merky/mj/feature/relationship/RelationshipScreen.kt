package icu.merky.mj.feature.relationship

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RelationshipScreen(
    uiState: RelationshipUiState,
    onIncreaseAffection: () -> Unit,
    onDecreaseAffection: () -> Unit,
    onIncreaseTrust: () -> Unit,
    onDecreaseTrust: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Relationship Panel",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(text = "Affection: ${uiState.state.affection}")
        Text(text = "Trust: ${uiState.state.trust}")
        Text(text = "Mood: ${uiState.state.mood}")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onIncreaseAffection, modifier = Modifier.weight(1f)) {
                Text("Affection +")
            }
            Button(onClick = onDecreaseAffection, modifier = Modifier.weight(1f)) {
                Text("Affection -")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onIncreaseTrust, modifier = Modifier.weight(1f)) {
                Text("Trust +")
            }
            Button(onClick = onDecreaseTrust, modifier = Modifier.weight(1f)) {
                Text("Trust -")
            }
        }
    }
}
