/**
 * These optimizations are targeted at improving sampling speed, mostly by reducing sampling when the result is discarded anyway.
 * This disables Lithium's 'mixin.gen.fast_layer_sampling' since it has an even faster Overwrite in ScaleLayerMixin
 */
package me.contaria.glacier.mixin.gen.biome_layers;