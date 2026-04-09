package com.example.demo.feature.game.home.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.demo.domain.model.tasks.PermanentTasks
import com.example.demo.domain.model.tasks.Task
import com.example.demo.domain.model.tasks.TaskCategory
import com.example.demo.domain.model.tasks.TaskTemplate
import com.example.demo.domain.theme.AppThemeId
import com.example.demo.domain.theme.ThemeMode
import com.example.demo.domain.util.formatSaveTimestamp
import com.example.demo.feature.game.utils.getSkillIcon
import com.example.demo.ui.theme.DemoTheme
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock

@Composable
fun WorkshopScreen(
    activeTasks: List<Task>,
    startTask: (TaskTemplate) -> Unit,
    modifier: Modifier = Modifier,
) {
    var backpackVisible by remember { mutableStateOf(false) }
    Box(modifier) {
        Box(
            modifier = Modifier.fillMaxHeight()
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                WorkshopSubScreenButton(
                    "B",
                    Color(0xFFFFA700),
                    open = { backpackVisible = !backpackVisible }
                )
                WorkshopSubScreenButton(
                    "T",
                    Color(0xFFC0C0C0),
                    open = {}
                )
            }
        }
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            Text("Active Task")
            Column(

            ) {
                activeTasks.forEach { task ->
                    Column {
                        Text(task.name)
                        Text(task.uuid)
                        Text(formatSaveTimestamp(task.startedAt.times(1000)))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth((task.workCompleted / task.totalWork).toFloat())
                                    .height(5.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                            ) {

                            }
                        }
                    }
                }
            }

            Text("Available Tasks")
            LazyColumn {
                items(PermanentTasks.allTasks.toList()) { (name, task) ->
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column {
                                    Text(
                                        text = name.uppercase(),
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                    Text(
                                        text = task.description,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Box(
                                    modifier = Modifier.background(Color(0xFFAA3333))
                                        .padding(horizontal = 4.dp),
                                ) {
                                    Text(
                                        text = task.totalWork.toString() + " WORK",
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                            if (task.reqStatLevels.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row {
                                        task.reqStatLevels.forEach { reqStatData ->
                                            Row(
                                                modifier = Modifier
                                                    .background(MaterialTheme.colorScheme.background)
                                                    .border(
                                                        width = 0.5.dp,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                    .padding(4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Box {
                                                    Image(
                                                        painter = painterResource(reqStatData.stat.getSkillIcon()),
                                                        contentDescription = reqStatData.stat.name,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                Box {
                                                    Text(
                                                        text = "Lvl " + reqStatData.minLevel.toString(),
                                                        style = MaterialTheme.typography.bodySmall,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    Column(
                                        modifier = Modifier.border(
                                            width = 0.5.dp,
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        ).background(MaterialTheme.colorScheme.background)
                                    ) {
                                        task.outputItems.forEach { outputItem ->
                                            Box(
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            ) {
                                                Text(
                                                    text = outputItem.item.name + " x" + outputItem.quantity.toString(),
                                                    style = MaterialTheme.typography.bodySmall,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .clickable {
                                        startTask(task)
                                    }
                            ) {
                                Text(
                                    text = "START",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                    }
                }
            }
        }
        BackpackSubScreen(
            visible = backpackVisible,
            close = { backpackVisible = false },
        )
    }
}

@Composable
fun BackpackSubScreen(
    visible: Boolean,
    close: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { -it },
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { -it },
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "<",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxHeight().width(80.dp).clickable { close() }
                    )
                    Text(
                        text = "BACKPACK",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                    )
                    Box(
                        modifier = Modifier.fillMaxHeight().width(80.dp)
                    ) {}
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun WorkshopSubScreenButton(
    text: String, // TODO: Swap for icon
    color: Color,
    open: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(color = color)
            .clip(
                RoundedCornerShape(
                    topStart = CornerSize(0.dp),
                    topEnd = CornerSize(4.dp),
                    bottomEnd = CornerSize(4.dp),
                    bottomStart = CornerSize(0.dp)
                )
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = RoundedCornerShape(
                    topStart = CornerSize(0.dp),
                    topEnd = CornerSize(4.dp),
                    bottomEnd = CornerSize(4.dp),
                    bottomStart = CornerSize(0.dp)
                )
            )
            .clickable {
                open()
            }
        ,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(
    showBackground = true,
    heightDp = 800,
    widthDp = 450,
)
@Composable
fun TasksScreenPreview() {
    DemoTheme(
        themeId = AppThemeId.DEFAULT,
        mode = ThemeMode.LIGHT
    ) {
        WorkshopScreen(
            startTask = {},
            activeTasks = listOf(
                Task(
                    uuid = "UUID",
                    name = "Task 1",
                    startedAt = Clock.System.now().epochSeconds,
                    description = "Tast Description",
                    category = TaskCategory.Production.Mining,
                    reqStatLevels = emptyList(),
                    workPerTick = 0.0,
                    totalWork = 10.0,
                    workCompleted = 3.0,
                    tags = emptyList(),
                    reqItems = emptyList(),
                    outputItems = emptyList(),
                    experienceGain = emptyList(),
                    isBackground = false
                )
            )
        )
    }
}