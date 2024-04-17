package ru.glazunov.habitstracker.models

class HabitListViewModelState (
    var searchPrefix: String = "",
    var ordering: Ordering = Ordering.Ascending
)