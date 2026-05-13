package com.example.waterlevelcontroller.core.utils

import kotlin.math.abs
import kotlin.math.floor


/**
 * Largest Triangle Three Bucket Algorithm
 */
object Lttb {
    fun calculate(data: List<Pair<Double, Double>>, threshold: Int): List<Pair<Double, Double>> {
        val size = data.size
        if (threshold >= size || threshold == 0) return data

        val sampled = mutableListOf<Pair<Double, Double>>()
        sampled.add(data[0]) // Always include the first point

        val bucketSize = (size - 2).toDouble() / (threshold - 2)
        var a = 0

        for (i in 0 until threshold - 2) {
            var avgX = 0.0
            var avgY = 0.0
            val avgRangeStart = (floor((i + 1) * bucketSize) + 1).toInt()
            val avgRangeEnd = (floor((i + 2) * bucketSize) + 1).toInt().coerceAtMost(size)
            val avgRangeLength = avgRangeEnd - avgRangeStart

            for (j in avgRangeStart until avgRangeEnd) {
                avgX += data[j].first
                avgY += data[j].second
            }
            avgX /= avgRangeLength
            avgY /= avgRangeLength

            val rangeOffs = (floor(i * bucketSize) + 1).toInt()
            val rangeTo = (floor((i + 1) * bucketSize) + 1).toInt()

            val pointAx = data[a].first
            val pointAy = data[a].second
            var maxArea = -1.0
            var nextA = rangeOffs

            for (j in rangeOffs until rangeTo) {
                val area = abs(
                    (pointAx - avgX) * (data[j].second - pointAy) -
                            (pointAx - data[j].first) * (avgY - pointAy)
                ) * 0.5
                if (area > maxArea) {
                    maxArea = area
                    nextA = j
                }
            }

            sampled.add(data[nextA])
            a = nextA
        }

        sampled.add(data[size - 1]) // Always include the last point
        return sampled
    }
}