package ifs.fultxw.engine.util;

import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;


public class LuceneUtil {
	private static Analyzer analyzer = null;
	public static Analyzer getCreateAnalyzerInstance() {
		if (analyzer == null)
			return  new IKAnalyzer();
		else
			return analyzer;
	}
}
