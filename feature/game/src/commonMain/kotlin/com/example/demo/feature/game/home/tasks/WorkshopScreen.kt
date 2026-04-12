package com.example.demo.feature.game.home.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.demo.domain.model.items.ItemInstance
import com.example.demo.domain.model.skills.CharacterStat
import com.example.demo.domain.model.tasks.PermanentTasks
import com.example.demo.domain.model.tasks.Task
import com.example.demo.domain.model.tasks.TaskCategory
import com.example.demo.domain.model.tasks.TaskTemplate
import com.example.demo.domain.theme.AppThemeId
import com.example.demo.domain.theme.ThemeMode
import com.example.demo.domain.util.roundToDecimals
import com.example.demo.feature.game.utils.getSkillIcon
import com.example.demo.ui.theme.DemoTheme
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.scope.Scope
import kotlin.math.floor
import kotlin.time.Clock

@Composable
fun WorkshopScreenRoot(
    gameScope: Scope,
    startTask: (TaskTemplate) -> Unit,
    collectTask: (Task) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel : WorkshopViewModel = koinViewModel(
        scope = gameScope
    )
    val activeTasks by viewModel.activeTasks.collectAsStateWithLifecycle()
    val storageItems by viewModel.storageItems.collectAsStateWithLifecycle()

    WorkshopScreen(
        activeTasks = activeTasks.filter { !it.collected },
        storageItems = storageItems,
        startTask = startTask,
        collectTask = collectTask,
        modifier = modifier,
    )
}

private enum class SubScreens {
    MINING,
    WOODCUTTING,
    FISHING,
    FARMING
}

@Composable
fun WorkshopScreen(
    activeTasks: List<Task>,
    storageItems: List<ItemInstance>,
    startTask: (TaskTemplate) -> Unit,
    collectTask: (Task) -> Unit,
    modifier: Modifier = Modifier,
) {
    var backpackVisible by remember { mutableStateOf(false) }
    var subscreenVisible by remember { mutableStateOf<SubScreens?>(null) }
    Box(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Active Tasks",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items = activeTasks, key = { it.uuid }) { task ->
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(16.dp)
                            .clickable {
                                if (task.workCompleted >= task.totalWork) { collectTask(task) }
                            }.
                            animateItem(
                                fadeInSpec = spring(stiffness = StiffnessLow),
                                fadeOutSpec = spring(stiffness = StiffnessLow),
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(task.name)
                            if (task.workCompleted >= task.totalWork) {
                                Text(
                                    text = "TAP TO COLLECT",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium,
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth((task.workCompleted / task.totalWork).toFloat())
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Text(
                                    text = floor(task.workCompleted).toInt().toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                WorkshopMenuButton(
                    name = "Backpack",
                    color = Color(0xFFFFA700),
                    open = { backpackVisible = !backpackVisible },
                    modifier = Modifier.fillMaxWidth()
                )
                WorkshopMenuDivider(
                    name = "Raw Resources",
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    WorkshopMenuButton(
                        name = "Mining",
                        color = Color(0xFFFFA700),
                        open = { subscreenVisible = SubScreens.MINING },
                        modifier = Modifier.weight(1f)
                    )
                    WorkshopMenuButton(
                        name = "Woodcutting",
                        color = Color(0xFFAA7722),
                        open = {},
                        modifier = Modifier.weight(1f)
                    )
                    WorkshopMenuButton(
                        name = "Fishing",
                        color = Color(0xFF22AAFF),
                        open = {},
                        modifier = Modifier.weight(1f)
                    )
                    WorkshopMenuButton(
                        name = "Farming",
                        color = Color(0xFF33BB33),
                        open = {},
                        modifier = Modifier.weight(1f)
                    )
                }
                WorkshopMenuDivider(
                    name = "Facilities",
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    WorkshopMenuButton(
                        name = "Workbench",
                        color = Color(0xFFC0C0C0),
                        open = {},
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        BackpackSubScreen(
            storageItems = storageItems,
            visible = backpackVisible,
            close = { backpackVisible = false },
        )
        RawResourceTaskList(
            visible = subscreenVisible == SubScreens.MINING,
            name = "MINING",
            taskList = PermanentTasks.allTasks
                .filter {
                    it.value.category == TaskCategory.Production.Mining
                }
                .map {
                    it.value
                },
            startTask = startTask,
            close = { subscreenVisible = null }
        )
        RawResourceTaskList(
            visible = subscreenVisible == SubScreens.WOODCUTTING,
            name = "WOODCUTTING",
            taskList = PermanentTasks.allTasks
                .filter {
                    it.value.category == TaskCategory.Production.Woodcutting
                }
                .map {
                    it.value
                },
            startTask = startTask,
            close = { subscreenVisible = null }
        )
    }
}

@Composable
fun WorkshopMenuDivider(
    name: String,
    modifier: Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant).height(1.dp).weight(1f)
        )
        Text(
            text = name,
        )
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant).height(1.dp).weight(1f)
        )
    }
}

@Composable
fun WorkshopMenuButton(
    name: String,
    color: Color,
    open: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .clickable { open() }
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun BackpackSubScreen(
    storageItems: List<ItemInstance>,
    visible: Boolean,
    close: () -> Unit,
) {
    val groupedItems = storageItems.groupBy { it.name }.toList()
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "BACKPACK",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f).padding(vertical = 4.dp)
                )
                HorizontalDivider()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                        .padding(horizontal = 4.dp, vertical = 8.dp)
                ) {
                    groupedItems.forEach { item ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text =
                                if (item.second.size > 1) {
                                    "${item.first} (${item.second.size})"
                                } else {
                                    item.first
                                }
                            )
                            Text(
                                text =
                                    item
                                        .second
                                        .minOf { it.quality ?: 0.0 }
                                        .roundToDecimals(2)
                                        .toString()
                                            + "-" +
                                            item
                                                .second
                                                .maxOf { it.quality ?: 0.0 }
                                                .roundToDecimals(2)
                                                .toString()
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(vertical = 4.dp)
                        .clickable {
                            close()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CLOSE",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
fun RawResourceTaskList(
    visible: Boolean,
    name: String,
    taskList: List<TaskTemplate>,
    startTask: (TaskTemplate) -> Unit,
    close: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                items(taskList) { task ->
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
                                        text = task.name.uppercase(),
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
                                    Row(
                                        modifier = Modifier.weight(0.5f)
                                    ) {
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
                                        ).background(MaterialTheme.colorScheme.background).weight(0.5f)
                                    ) {
                                        task.outputItems.forEach { outputItem ->
                                            Box(
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            ) {
                                                Text(
                                                    text = outputItem.itemTemplate.name + " x" + outputItem.quantity.toString(),
                                                    style = MaterialTheme.typography.bodySmall,
                                                )
                                            }
                                        }
                                        HorizontalDivider()
                                        task.experienceGain.forEach { experienceGain ->
                                            Box(
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            ) {
                                                Row {
                                                    Image(
                                                        painter = painterResource(experienceGain.second.getSkillIcon()),
                                                        contentDescription = experienceGain.second.name,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Text(
                                                        text =  "+" + experienceGain.first.toString() + "xp",
                                                        style = MaterialTheme.typography.bodySmall,
                                                    )
                                                }
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
                                        close()
                                    }
                            ) {
                                Text(
                                    text = "START",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().clickable { close() }
            ) {
                Text(
                    text = "CLOSE",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
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
            collectTask = {},
            storageItems = listOf(

            ),
            activeTasks = listOf(
                Task(
                    uuid = "UUID 1",
                    name = "Task 1",
                    startedAt = Clock.System.now().epochSeconds,
                    description = "Tast Description",
                    category = TaskCategory.Production.Mining,
                    reqStatLevels = emptyList(),
                    workPerSecond = 0.0,
                    totalWork = 10.0,
                    workCompletedPerCharacter = emptyList(),
                    tags = emptyList(),
                    reqItems = emptyList(),
                    outputItems = emptyList(),
                    experienceGain = listOf(
                        5 to CharacterStat.Mining
                    ),
                    isBackground = false,
                    collected = false,
                ),
                Task(
                    uuid = "UUID 2",
                    name = "Task 2",
                    startedAt = Clock.System.now().epochSeconds,
                    description = "Tast Description",
                    category = TaskCategory.Production.Woodcutting,
                    reqStatLevels = emptyList(),
                    workPerSecond = 0.0,
                    totalWork = 10.0,
                    workCompletedPerCharacter = emptyList(),
                    tags = emptyList(),
                    reqItems = emptyList(),
                    outputItems = emptyList(),
                    experienceGain = listOf(
                        5 to CharacterStat.Woodcutting
                    ),
                    isBackground = false,
                    collected = false
                )
            )
        )
    }
}

@Preview
@Composable
fun RawResourceTaskListPreview() {
    RawResourceTaskList(
        name = "Task List",
        visible = true,
        taskList = PermanentTasks.allTasks.map { it.value },
        startTask = {},
        close = {}
    )
}