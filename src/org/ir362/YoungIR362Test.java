package org.ir362;
import org.ir362.indexing.CorpusIndexMaker;
import org.ir362.indexing.DiskIndexManager;
import org.ir362.indexing.InvertedIndex;
import org.ir362.matching.ResultSet;
import org.ir362.querying.Manager;
import org.ir362.querying.Request;
import edu.uci.jforests.util.Pair;


class YoungIR362Test {
    public static final void main(String args[]) throws Exception {

        InvertedIndex index = new CorpusIndexMaker().makeIndexFromCorpus(CorpusIndexMaker.corpus_folder);
        //InvertedIndex index = new DiskIndexManager().loadIndexFromDisk();;

		Manager queryingManager = new Manager(index);

		String query = "体育";
		Request rq = queryingManager.newRequest(query);

		queryingManager.preProcess(rq);  // we obtain
		queryingManager.runMatching(rq);
		ResultSet rs = rq.getResultSet();

		int rank = 0;
		for(Pair<Integer, Double> p : rs.getResultArray())
		{
			rank++;
			System.out.println("Rank="+rank+"\n");		
            System.out.println("result: "+ "DocID="+ p.getFirst() + "\n");
			System.out.println("Score: "+ p.getSecond() + "\n");	
		}
    }
}