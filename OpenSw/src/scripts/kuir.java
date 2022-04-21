package scripts;

import java.io.EOFException;

public class kuir {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String command = args[0];
		String path = args[1];
		
		
		if (command.equals("-c")) { 
			makeCollection collection = new makeCollection(path);
			collection.makeXml();
		}
		else if (command.equals("-k")) { 
			makeKeyword keyword = new makeKeyword(path);
			keyword.convertXml();
		}
		else if (command.equals("-i")) {
			indexer indexer = new indexer(path);
			indexer.makexml();
			indexer.makePost();
			indexer.readPost();

		}
		else if (command.equals("-s")) {
			if (args.length < 4) { 
				System.out.println("명령어를 잘 못 입력하셨습니다.");
			}
			else { 
				String question = args[3];
				searcher searcher = new searcher(question,path);
				searcher.search();
			}
			
		}
		else if (command.equals("-m")) { 
			if (args.length < 4) { 
				System.out.println("명령어를 잘 못 입력하셨습니다.");
			}
			else { 
				String question = args[3];
				MidTerm midterm = new MidTerm(path,question);
			}
		}
	}

}
