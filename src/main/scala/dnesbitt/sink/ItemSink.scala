package dnesbitt.sink

import com.google.cloud.bigquery.TimePartitioning.Type
import com.google.cloud.bigquery.{Field, _}

/**
  * @author Daniel Nesbitt.
  */
class ItemSink {

  def foo(): Unit = {
    val bigquery = BigQueryOptions.getDefaultInstance.getService

    val datasetName = "Path of Exile"

    val datasetInfo = DatasetInfo.newBuilder(datasetName)
      .build

    // Creates the dataset
    var dataset = bigquery.create(datasetInfo)

    val foo = Schema.newBuilder()
      .addField(Field.of("id", Field.Type.string()))
      .addField(Field.of("stash-id", Field.Type.string()))
      .build()

    val definition = StandardTableDefinition.newBuilder()
      .setTimePartitioning(TimePartitioning.of(Type.DAY))
      .setSchema(foo)
      .build()
    val table = dataset.create("", definition)

    System.out.printf("Dataset %s created.%n", dataset.getDatasetId.getDataset)
  }

}
