package com.braveboy.twopane.model

interface TwoPaneNavNode {
    val id: String
    val title: String
    val summary: String
    val value: String?
    val children: List<TwoPaneNavNode>

    val isLeaf: Boolean get() = children.isEmpty()
}
