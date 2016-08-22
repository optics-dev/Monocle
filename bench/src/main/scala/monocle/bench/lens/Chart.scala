package monocle.bench.lens

import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.{ChartFactory, JFreeChart}
import org.jfree.data.category.DefaultCategoryDataset

object Chart {

  def save(matrix: Matrix): JFreeChart = {
    val dataSet = new DefaultCategoryDataset()
    matrix.value.toList.sortBy(_._1).foreach{ case (method, row) =>
      row.impls.toList.sortBy(_._1).foreach{ case (impl, result) =>
        dataSet.addValue(result.score, impl.toString, method.toString)
      }
    }
    ChartFactory.createBarChart(
      "Lens Impl",
      "Methods",
      "Ops/sec",
      dataSet,
      PlotOrientation.VERTICAL,
      true, true, false
    )
  }

  def save(matrix: NormalisedMatrix): JFreeChart = {
    val dataSet = new DefaultCategoryDataset()
    matrix.value.toList.sortBy(_._1).foreach{ case (method, row) =>
      row.impls.toList.sortBy(_._1).foreach{ case (impl, ratio) =>
        dataSet.addValue(ratio, impl.toString, method.toString)
      }
    }
    ChartFactory.createBarChart(
      "Lens Impl",
      "Methods",
      "Ratio",
      dataSet,
      PlotOrientation.VERTICAL,
      true, true, false
    )
  }
}
