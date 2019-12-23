import unittest
import fractal

class HistogramLayerTester(unittest.TestCase):

    def test_toString(self):
        layer = fractal.HistogramLayer()
        desc = str(layer)
        self.assertEqual("histogramLayer", desc)

    def test_opacity(self):
        layer = fractal.HistogramLayer()

        self.assertEqual(1, layer.opacity)
        layer.opacity = 0.2
        self.assertEqual(0.2, round(layer.opacity, 5))

    def test_name(self):
        layer = fractal.HistogramLayer()

        self.assertEqual("My Layer", layer.name)
        layer.name = "Layer 1"
        self.assertEqual("Layer 1", layer.name)

    def test_is_visible(self):
        layer = fractal.HistogramLayer()

        self.assertEqual(True, layer.is_visible)
        layer.is_visible = False
        self.assertEqual(False, layer.is_visible)

    def test_palette(self):
        layer = fractal.HistogramLayer()

        # Ensure that the getter points to the real one
        pal = layer.palette
        pal.add_color((1,1,1), 0.5)
        self.assertEqual(len(layer.palette.get_colors()), 3)

        # Ensure the setter works
        newPal = fractal.Palette()
        newPal.remove_color(0)
        layer.palette = newPal
        self.assertEqual(len(layer.palette.get_colors()), 1)

    def test_palette_memory_leak(self):
        layer = fractal.HistogramLayer()
        new_palette = fractal.Palette()

        layer.palette = new_palette
        layer_palette = layer.palette

        self.assertEqual(new_palette, layer_palette)


class FractalTester(unittest.TestCase):

    def test_toString(self):
        frac = fractal.Fractal()
        layer = fractal.HistogramLayer()
        frac.insert_layer(0, layer)

        desc = str(frac)
        self.assertEqual("fractal: \n\thistogramLayer\n", desc)

    def test_x(self):
        frac = fractal.Fractal()

        self.assertEqual(0, frac.x)
        frac.x = 1.11
        self.assertEqual(1.11, frac.x)

    def test_y(self):
        frac = fractal.Fractal()

        self.assertEqual(0, frac.y)
        frac.y = 1.11
        self.assertEqual(1.11, frac.y)

    def test_width(self):
        frac = fractal.Fractal()

        frac.width = 1000
        self.assertEqual(1000, frac.width)
        frac.width = 1001
        self.assertEqual(1001, frac.width)

    def test_height(self):
        frac = fractal.Fractal()

        frac.height = 1000
        self.assertEqual(1000, frac.height)
        frac.height = 1001
        self.assertEqual(1001, frac.height)

    def test_viewport_width(self):
        frac = fractal.Fractal()

        self.assertEqual(4, frac.viewport_width)
        frac.viewport_width = 1.11
        self.assertEqual(1.11, frac.viewport_width)

    # TODO: test adding in between two layers and at front of list
    def test_insert_layer(self):
        frac = fractal.Fractal()
        layer = fractal.HistogramLayer()
        frac.insert_layer(0, layer)

        self.assertEqual(1, frac.layer_count())

    def test_get_layer_functionality(self):
        frac = fractal.Fractal()
        layer = fractal.HistogramLayer()
        frac.insert_layer(0, layer)

        retrieved_layer = frac.get_layer(0)
        retrieved_layer.opacity = 0.2

        self.assertEqual(retrieved_layer.opacity, layer.opacity)

    def test_get_layer_memory_leak(self):
        frac = fractal.Fractal()
        layer = fractal.HistogramLayer()
        frac.insert_layer(0, layer)

        retrieved_layer = frac.get_layer(0)

        self.assertEqual(retrieved_layer, layer)

    def test_get_layer_invalid(self):
        frac = fractal.Fractal()

        self.assertRaises(IndexError, frac.get_layer, 0)
        self.assertRaises(IndexError, frac.get_layer, -1)

    def test_remove_layer_functionality(self):
        frac = fractal.Fractal()
        layer = fractal.HistogramLayer()

        # Insert then delete layer
        frac.insert_layer(0, layer)
        deleted_layer = frac.remove_layer(0)

        # Assert list length and equality of deleted layer
        self.assertEqual(0, frac.layer_count())
        deleted_layer.opacity = 0.2
        self.assertEqual(deleted_layer.opacity, layer.opacity)

    def test_remove_layer_memory_leak(self):
        frac = fractal.Fractal()
        layer = fractal.HistogramLayer()

        # Insert then delete layer
        frac.insert_layer(0, layer)
        deleted_layer = frac.remove_layer(0)

        self.assertEqual(deleted_layer, layer)

    def test_remove_layer_invalid(self):
        frac = fractal.Fractal()

        self.assertRaises(IndexError, frac.remove_layer, 0)
        self.assertRaises(IndexError, frac.remove_layer, -1)

    def test_insert_layer_invalid(self):
        frac = fractal.Fractal()
        layer = fractal.HistogramLayer()

        self.assertRaises(IndexError, frac.insert_layer, 1, layer)
        self.assertRaises(IndexError, frac.insert_layer, -1, layer)


class PaletteTester(unittest.TestCase):

    def test_toString(self):
        palette = fractal.Palette()
        self.assertEqual("palette string", str(palette))

    def test_interior_color_valid(self):
        palette = fractal.Palette()
        self.assertEqual(palette.interior_color, (255, 255, 255))

        palette.interior_color = (10, 11, 12)
        self.assertEqual(palette.interior_color, (10, 11, 12))

    def test_color_at_valid(self):
        palette = fractal.Palette()

        # Test only a single color
        palette.remove_color(0)
        self.assertEqual(palette.color_at(0), (255, 255, 255))

        palette.add_color((0, 0, 0), 0)

        self.assertEqual(palette.color_at(0.5), ((int)(255/2), (int)(255/2), (int)(255/2)))
        self.assertEqual(palette.color_at(1), (255, 255, 255))

        palette.add_color((0, 0, 0), 0.9)
        self.assertEqual(palette.color_at(0.5), (0, 0, 0))

    def test_opacity_at_valid(self):
        palette = fractal.Palette()

        # Test only a single opacity
        palette.remove_opacity(0)
        self.assertEqual(palette.opacity_at(0.5), 1)

        # Test basic interpolation
        palette.add_opacity(0, 0)
        self.assertEqual(palette.opacity_at(0.5), 0.5)

    def test_add_color_valid(self):
        palette = fractal.Palette()

        result = palette.add_color((0, 0, 0), 0.0009) and palette.add_color((0, 0, 0), 0.00091)
        self.assertEqual(result, True)

    def test_add_color_invalid_location(self):
        palette = fractal.Palette()

        # Test for duplicate location
        result = palette.add_color((0, 0, 0), 0.0)
        self.assertEqual(result, False)

        # Test for above and below valid location
        result = palette.add_color((0, 0, 0), -0.001)
        self.assertEqual(result, False)
        result = palette.add_color((0, 0, 0), 1.001)
        self.assertEqual(result, False)

    def test_add_color_invalid_color(self):
        palette = fractal.Palette()

        # Test with negative color
        self.assertEqual(palette.add_color((-1, 0, 0), 0.5), False)
        # Test with too high a color color
        self.assertEqual(palette.add_color((0, 256, 0), 0.5), False)
        # Test with a float value
        self.assertRaises(AttributeError, palette.add_color, (0, 0, 0.5), 0.5)

    def test_add_opacity_valid(self):
        palette = fractal.Palette()

        result = palette.add_opacity(0.5, 0.0009) and palette.add_opacity(0.5, 0.00091)
        self.assertEqual(result, True)

    def test_add_opacity_invalid_location(self):
        palette = fractal.Palette()

        # Test for duplicate location
        result = palette.add_opacity(0, 0.0)
        self.assertEqual(result, False)

        # Test for above and below valid location
        result = palette.add_opacity(0, -0.001)
        self.assertEqual(result, False)
        result = palette.add_opacity(0, 1.001)
        self.assertEqual(result, False)

    def test_add_opacity_invalid_opacity(self):
        palette = fractal.Palette()

        # Test with negative opacity
        self.assertEqual(palette.add_opacity(-0.0001, 0.5), False)
        # Test with too high a opacity
        self.assertEqual(palette.add_opacity(1.0001, 0.5), False)
        # Test with too high a non-numerical value
        self.assertRaises(AttributeError, palette.add_opacity, "o", 0.5)

    def test_remove_color_valid(self):
        palette = fractal.Palette()

        result = palette.remove_color(0)
        self.assertEqual(result, True)
        self.assertEqual(palette.color_at(0.5), (255, 255, 255))

    def test_remove_color_invalid_location(self):
        palette = fractal.Palette()

        # Test for invalid location within valid location range
        result = palette.remove_color(0.5)
        self.assertEqual(result, False)

        # Test for above and below valid location range
        result = palette.remove_color(-0.001)
        self.assertEqual(result, False)
        result = palette.remove_color(1.001)
        self.assertEqual(result, False)

    def test_remove_opacity_valid(self):
        palette = fractal.Palette()

        # Check if we can remove valid opacity
        result = palette.remove_opacity(0)
        self.assertEqual(result, True)
        self.assertEqual(len(palette.get_opacities()), 1)
        self.assertEqual(palette.get_opacities(), [(1, 1)])

    def test_remove_opacity_invalid_location(self):
        palette = fractal.Palette()

        # Test for invalid location within valid location range
        result = palette.remove_opacity(0.5)
        self.assertEqual(result, False)

        # Test for above and below valid location range
        result = palette.remove_opacity(-0.001)
        self.assertEqual(result, False)
        result = palette.remove_opacity(1.001)
        self.assertEqual(result, False)

    def test_color_at_when_empty(self):
        palette = fractal.Palette()

        palette.remove_color(0)
        palette.remove_color(1)
        palette.interior_color = (10, 11, 12)

        self.assertEqual(palette.color_at(0.5), (10, 11, 12))

    def test_opacity_at_when_empty(self):
        palette = fractal.Palette()

        palette.remove_opacity(0)
        palette.remove_opacity(1)

        self.assertEqual(palette.opacity_at(0.5), 1)

    def test_get_colors(self):
        palette = fractal.Palette()
        palette.add_color((100, 100, 100), 0.5)

        color_list = palette.get_colors()

        self.assertEqual(len(color_list), 3)
        self.assertEqual(color_list[0], (0, 0, 0, 0))
        self.assertEqual(color_list[1], (100, 100, 100, 0.5))
        self.assertEqual(color_list[2], (255, 255, 255, 1))

    def test_get_colors(self):
        palette = fractal.Palette()
        palette.add_opacity(0.2, 0.5)

        opacity_list = palette.get_opacities()

        self.assertEqual(len(opacity_list), 3)
        self.assertEqual(opacity_list[0], (1, 0))
        self.assertEqual(opacity_list[1], (0.2, 0.5))
        self.assertEqual(opacity_list[2], (1, 1))

if __name__ == '__main__':
    unittest.main()
