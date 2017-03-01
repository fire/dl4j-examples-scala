package org.deeplearning4j.examples.nlp.paragraphvectors

import org.datavec.api.util.ClassPathResource
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory
import org.nd4j.linalg.ops.transforms.Transforms
import org.slf4j.{Logger, LoggerFactory}

/**
  * This is example code for dl4j ParagraphVectors inference use implementation.
  * In this example we load previously built model, and pass raw sentences, probably never seen before, to get their vector representation.
  *
  *
  * *************************************************************************************************
  * PLEASE NOTE: THIS EXAMPLE REQUIRES DL4J/ND4J VERSIONS >= 0.6.0 TO COMPILE SUCCESSFULLY
  * *************************************************************************************************
  *
  * @author raver119@gmail.com
  */
object ParagraphVectorsInferenceExample {
  private val log: Logger = LoggerFactory.getLogger(ParagraphVectorsInferenceExample.getClass)

  @throws[Exception]
  def main(args: Array[String]) {
    val resource = new ClassPathResource("/paravec/simple.pv")
    val t = new DefaultTokenizerFactory
    t.setTokenPreProcessor(new CommonPreprocessor)

    // we load externally originated model
    val vectors = WordVectorSerializer.readParagraphVectors(resource.getFile)
    vectors.setTokenizerFactory(t)
    vectors.getConfiguration.setIterations(1) // please note, we set iterations to 1 here, just to speedup inference

    /*
    // here's alternative way of doing this, word2vec model can be used directly
    // PLEASE NOTE: you can't use Google-like model here, since it doesn't have any Huffman tree information shipped.

    ParagraphVectors vectors = new ParagraphVectors.Builder()
        .useExistingWordVectors(word2vec)
        .build();
    */
    // we have to define tokenizer here, because restored model has no idea about it



    val inferredVectorA = vectors.inferVector("This is my world .")
    val inferredVectorA2 = vectors.inferVector("This is my world .")
    val inferredVectorB = vectors.inferVector("This is my way .")

    // high similarity expected here, since in underlying corpus words WAY and WORLD have really close context
    log.info("Cosine similarity A/B: {}", Transforms.cosineSim(inferredVectorA, inferredVectorB))

    // equality expected here, since inference is happening for the same sentences
    log.info("Cosine similarity A/A2: {}", Transforms.cosineSim(inferredVectorA, inferredVectorA2))
  }
}