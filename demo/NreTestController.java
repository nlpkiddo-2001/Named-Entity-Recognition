package com.example.demo;
import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.CoreDocument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

 class NerEntities {
	  private String word;
	  private String label;
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	  
	public NerEntities(String word, String label)
	{
		this.word = word;
		this.label = label;
	}
	}
 	class InputData {
	    private String id;
	    private String text;

	    public InputData() {
	    }

	    public InputData(String id, String text) {
	        this.id = id;
	        this.text = text;
	    }

	    public String getId() {
	        return id;
	    }

	    public void setId(String id) {
	        this.id = id;
	    }

	    public String getText() {
	        return text;
	    }

	    public void setText(String text) {
	        this.text = text;
	    }
	}
	  class DataResponse {
		  private String id;
		  private List<NerEntities> nerEntities;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public List<NerEntities> getNerEntities() {
			return nerEntities;
		}
		public void setNerEntities(List<NerEntities> nerEntities) {
			this.nerEntities = nerEntities;
		}
	  }
	

@RestController
public class NreTestController {
    @PostMapping("/test2")
    public ResponseEntity<Map<String, List<DataResponse>>> ner2(@RequestBody Map<String, List<InputData>> inputs) throws IOException
    {
    	String fileName = "ner_test"+ System.nanoTime() +".txt";
    	BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
    	long memoryTaken = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
    	System.out.println("memory status before starting the appication " + memoryTaken);
    	bw.write("memory status before starting the application: " + memoryTaken + "\n");
    	long totalStartTime = System.currentTimeMillis();
    	StanfordCoreNLP stanfordCoreNlp = PipeLine.getPipeLine();
    	List<DataResponse> nerResults = new ArrayList<>();
    	List<InputData> input = inputs.get("input");
    	for(int i = 0; i < input.size(); i++)
    	{
    		long inputStartTime = System.currentTimeMillis();
    		CoreDocument document = new CoreDocument(input.get(i).getText());
    		stanfordCoreNlp.annotate(document);
    		List<NerEntities> nerEntities = new ArrayList<>();
    		List<CoreLabel> coreLabels = document.tokens();
    		for(CoreLabel coreLabel:coreLabels)
    		{
    			String ner = coreLabel.getString(CoreAnnotations.NamedEntityTagAnnotation.class);
    			nerEntities.add(new NerEntities(coreLabel.originalText(), ner));
    		}
    		long middleMemory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
    		System.out.println(" memory status at the middle of the appication " + middleMemory);
    		bw.write(" memory status =  " + middleMemory + "\n");
    		DataResponse nerResult = new DataResponse();
    		nerResult.setId(input.get(i).getId());
    		nerResult.setNerEntities(nerEntities);
    		nerResults.add(nerResult);
    		long inputEndTime = System.currentTimeMillis();
    		long inputTimeTaken = inputEndTime - inputStartTime;
    		System.out.println("time taken for " + i + " single input is " + inputTimeTaken);
    		bw.write(" time taken for " + i + input.get(i).getText() + " input is " + inputTimeTaken + " ms , ");
    		 
    	}
    	Map<String,List<DataResponse>> response = new HashMap<>();
    	String HttpResponseStatus = " HTTP " + HttpStatus.OK.value() + " " + HttpStatus.OK.getReasonPhrase();
    	response.put(HttpResponseStatus, nerResults);
    	long totalEndTime = System.currentTimeMillis();
    	long timeTaken = totalEndTime - totalStartTime;
    	long memoryAtEnd = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
    	System.out.println(" memory status after ending the appication " + memoryAtEnd);
    	bw.write(" memory at the end " + memoryAtEnd + "\n");
    	bw.write(" total time taken " + timeTaken);
    	System.out.println(" total Time taken = " + timeTaken + " ms ");
    	bw.close();
    	return ResponseEntity.ok(response);
    }
}
	

