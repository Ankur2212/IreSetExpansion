                          SET EXPANSION
						 ==============
OBJECTIVE
=========
The overall goal of this project is to use the World Wide Web as a source of information
to identify sets of words that are related in meaning.

RUNNING DETAILS
================
* Clone the code to your local machine.
* There are two parts in this project namely 
	1.Word2Vec model
	2.Set Expansion
* First step is to train the Word2Vec model. The dataset which we have used is related to 
  the computer science however it can be extended easily to other datasets.
  The "traindata.txt" contains approx 10000 words related computer science.
* After training the word2vec model a file is created which maps each word with its cosine
  angle.
* Now to perform the Set Expansion we make use of the "similarity" function of the word2vec
  model.
* Search the given seeds on a search engine like google, bing, yandex, faroo etc and retrieve
  the top results.
* Find the similarity between the current seed words and the words in retrieved webpages.
* Include the word which has the most similarity with the given seed words. This would be then
  new seed entry.
* This process continues until we get 10 seed words. The number of seeds required can also be changed.
* The output is stored in a file whose name is as given in the input.
  
CHALLENGES
==========
* How to extend traditional set expansion techniques to course related concepts
* How do you filter out concepts related to a different course
* Experiments need to be carefully designed because number of free API calls are limited.
* Precision vs Recall tradeoff.
* How to use other semantic similarity measures (if available) in addition to your approach
* When do you know that the set is full.
