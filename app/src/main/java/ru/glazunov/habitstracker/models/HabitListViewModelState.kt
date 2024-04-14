package ru.glazunov.habitstracker.models

class HabitListViewModelState (
    var searchPrefix: String = "",
    var habitType: HabitType = HabitType.POSITIVE,
    var ordering: Ordering = Ordering.Ascending
)