package TF_IDF;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class VectorSpaceModel {
	// 1. Integer: Document seq, 2. String: Term, 3. Double : frequency
	enum WriteOption {
		TF, TFIDF, DF
	}

	HashMap<Integer, LinkedHashMap<String, Double>> hash_tf = new LinkedHashMap<Integer, LinkedHashMap<String, Double>>();
	HashMap<String, Double> hash_df = new LinkedHashMap<String, Double>();
	HashMap<Integer, LinkedHashMap<String, Double>> hash_tfidf = new LinkedHashMap<Integer, LinkedHashMap<String, Double>>();
	FileReader fReader;
	BufferedReader bReader;

	public VectorSpaceModel(String wordListTxtFile) {
		try {

			fReader = new FileReader(wordListTxtFile);
			bReader = new BufferedReader(fReader);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void CalculateTFIDF() {

		try {
			String line = "";
			int numLine = 0;

			while ((line = bReader.readLine()) != null) {
				String[] splitedLine = line.split(" ");

				LinkedHashMap<String, Double> newTFHash = new LinkedHashMap<String, Double>();
				LinkedHashMap<String, Double> newDFHash = new LinkedHashMap<String, Double>();
				
				for (int i = 0; i < splitedLine.length; i++) {
					// TF
					if (newTFHash.containsKey(splitedLine[i]))
						newTFHash.replace(splitedLine[i], newTFHash.get(splitedLine[i]) + 1);
					else
						newTFHash.put(splitedLine[i], 1.0);

					// DF
					if (!newDFHash.containsKey(splitedLine[i]))
						newDFHash.put(splitedLine[i], 1.0);

				}
				// DF
				for (String key : newDFHash.keySet()) {
					if (hash_df.containsKey(key))
						hash_df.replace(key, hash_df.get(key) + 1);
					else
						hash_df.put(key, 1.0);
				}
				newDFHash.clear();

				hash_tf.put(numLine, newTFHash);

				numLine++;
				if (numLine % 499 == 0) {
					System.out.println(numLine);
				}
			}

			hash_tfidf = (HashMap<Integer, LinkedHashMap<String, Double>>) hash_tf.clone();
			for (Integer key : hash_tf.keySet()) {
				LinkedHashMap<String, Double> hashmap = hash_tf.get(key);
				for (String subkey : hashmap.keySet()) {
					double tf = Math.log(1 + hashmap.get(subkey));
					double idf = Math.log(numLine / hash_df.get(subkey));
					hash_tfidf.get(key).replace(subkey, tf*idf);
					
				}
			}

			bReader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void Write(String writefilePath,WriteOption option) {
		try {
			HashMap<Integer, LinkedHashMap<String, Double>> hash_writer = null;
			if(option == WriteOption.TF){
				hash_writer = hash_tf;
				
			}
			else if(option == WriteOption.TFIDF)
			{
				hash_writer= hash_tfidf;
			}
			FileWriter fWriter = new FileWriter(writefilePath);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			for (Integer key : hash_writer.keySet()) {
				HashMap<String, Double> hashmap = hash_tf.get(key);

				bWriter.write(key + "\t");

				for (String subkey : hashmap.keySet()) {

					bWriter.write(
							subkey + ":" + new DecimalFormat("##.###").format(hashmap.get(subkey)).toString() + " ");

				}

				bWriter.write("\n");

			}
			bWriter.close();
			fWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

