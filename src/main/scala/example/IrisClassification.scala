package  example 

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.StringIndexer

object IrisClassification {
  def main(args: Array[String]): Unit = {
   
    val spark: SparkSession = SparkSession
    .builder()
    .master("local[*]")
    .appName("IrisSparkML")
    .getOrCreate()
      
    spark.sparkContext.setLogLevel("error")

    val data = spark.read
      .option("header", true)
      .option("inferSchema", true)
      .csv("/home/bdron/source/IRIS.csv") 
    
    // Преобразуем столбец species в числовой формат
    val labelIndexer = new StringIndexer()
      .setInputCol("species")
      .setOutputCol("label")
      .fit(data)
    
    // Собираем признаки в один вектор
    val assembler = new VectorAssembler()
      .setInputCols(Array("sepal_length", "sepal_width", "petal_length", "petal_width"))
      .setOutputCol("features")
    
    // Используем RandomForest в качестве модели классификации
    val rf = new RandomForestClassifier()
      .setLabelCol("label")
      .setFeaturesCol("features")
    
    // Создаем пайплайн
    val pipeline = new Pipeline()
      .setStages(Array(labelIndexer, assembler, rf))
    
    // Разделяем данные на обучающую и тестовую части
    val Array(trainingData, testData) = data.randomSplit(Array(0.7, 0.3))
    
    // Обучаем модель на обучающих данных
    val model = pipeline.fit(trainingData)
    
    // Прогнозируем на тестовых данных
    val predictions = model.transform(testData)
    
    // Оцениваем качество модели
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(predictions)
    
    println(s"Коэффициент достоверности модели = ${accuracy}")
    
    // Сохраняем модель
    model.write.overwrite().save("/home/bdron/source/build/iris_model")
    
    // Закрываем Spark сессию
    spark.stop()
  }
}
