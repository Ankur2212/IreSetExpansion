/* Class for training the word2vec */


package com.team11.concept;

import java.io.File;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.EndingPreProcessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.SerializationUtils;

public class Word2VecTraining {
	private SentenceIterator iter;
	private Word2Vec vec;

	public final static String VEC_PATH = "vec2.ser";

	public Word2VecTraining() throws Exception {
		this.iter = new LineSentenceIterator(new File("TrainingDataSet.txt"));   // Training Dataset
	}

	public void word2VecTraining() throws Exception {
		if(vec == null && !new File(VEC_PATH).exists()) {
			System.out.println("!!!!!!!!!!!!!!!!!");
			iter.setPreProcessor(new SentencePreProcessor() {				// Process the given dataset line by line
				@Override
				public String preProcess(String sentence) {
					return sentence.toLowerCase();							
				}
			});

			TokenizerFactory t = new DefaultTokenizerFactory();
			final EndingPreProcessor preProcessor = new EndingPreProcessor();
			t.setTokenPreProcessor(new TokenPreProcess() {
				@Override
				public String preProcess(String token) {
					token = token.toLowerCase();
					String base = preProcessor.preProcess(token);		// obtain the tokenized words
					base = base.replaceAll("\\d", "d");
					if (base.endsWith("ly") || base.endsWith("ing"))
						System.out.println();
					return base;
				}
			});

			int layerSize = 300;
			vec = new Word2Vec.Builder().sampling(1e-5)
					.minWordFrequency(5).batchSize(1000).useAdaGrad(false).layerSize(layerSize)
					.iterations(3).learningRate(0.025).minLearningRate(1e-2).negativeSample(10)			
					.iterate(iter).tokenizerFactory(t).build();						// set the parameters for the word2vec model

			vec.fit();
			SerializationUtils.saveObject(vec, new File(VEC_PATH));
		}else{
			System.out.println("System Already Trained !!!!! :)");			
		}
	}
}
