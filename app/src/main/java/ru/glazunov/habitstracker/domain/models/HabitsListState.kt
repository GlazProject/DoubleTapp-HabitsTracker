package ru.glazunov.habitstracker.domain.models

class HabitsListState (
    var searchPrefix: String = "",
    var ordering: Ordering = Ordering.Ascending
)