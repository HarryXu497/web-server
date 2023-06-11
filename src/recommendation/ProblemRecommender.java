package recommendation;

import database.model.Problem;

import java.util.ArrayList;
import java.util.List;

/**
 * An algorithm to recommend problems based on the current one being viewed
 * @author Tommy Shan
 * @version 1.0 - May 23rd 2023
 */
public class ProblemRecommender {
    private final ArrayList<Problem> problems;

    /**
     * Constructs a {@link ProblemRecommender}
     * @param problems a list of problems to recommend from
     */
    public ProblemRecommender(ArrayList<Problem> problems) {
        this.problems = problems;
    }

    /**
     * getProblems
     * Method that generate numOfProblem of problems based on the given curProblem.
     * @param curProblem the problem that's used to find similar problem
     * @param numOfProblem the number of similar problems we wish to find
     * @return the ArrayList of relative problem
     */
    public List<Problem> getProblems(Problem curProblem, int numOfProblem) {
        ArrayList<Problem> problems = new ArrayList<>();

        int[] relativity = new int[this.problems.size()];

        for(int i = 0; i < this.problems.size(); i++) {
            relativity[i] = 0;
            // If the different between two problems' difficulty is equal to 0, the problem earn s10 relativity points. As the difference increased by 1, the relativity point decreased by 1, until 0.
            relativity[i] += Math.max(0, 10 - Math.abs(this.problems.get(i).getDifficulty() - curProblem.getDifficulty()));
            // If the author of the two problems are same, the problem earn 1 additional relativity point.
            relativity[i] += (this.problems.get(i).getAuthorID() == curProblem.getAuthorID() ? 1 : 0);
            // If the problem type of the two problems are same, the problem earn 5 additional relativity point.
            relativity[i] += (this.problems.get(i).getType().equals(curProblem.getType()) ? 5 : 0);
            // The proportion of same characters * 100 is added to the relativity point.
            relativity[i] += similarity(this.problems.get(i).getContent(), curProblem.getContent());

            if(this.problems.get(i).getProblemID() == curProblem.getProblemID()) {
                relativity[i] = -1;
            }
        }

        for(int i = 0; i < Math.min(this.problems.size(), numOfProblem); i++) {
            int maxPoint = -1, maxProblem = -1;
            for(int j = 0; j < this.problems.size(); j++) {
                if(maxPoint < relativity[j]) {
                    maxPoint = relativity[j];
                    maxProblem = j;
                }
            }
            relativity[maxProblem] = -1;

            problems.add(this.problems.get(maxProblem));
        }

        return problems;
    }

    /**
     * similarity
     * Compares the similarity between two problem's contents based on same characters
     * @param content1 the first content we want to compare
     * @param content2 the second content we want to compare
     * @return the similarity between two content in terms of the relativity point, the method can provide up to 100 points if two paragraph match exactly
     */
    public int similarity(String content1, String content2) {
        int ret = 0;

        int[] freq1 = new int[26];
        int[] freq2 = new int[26];

        for(int i = 0; i < content1.length(); i++) {
            if(Character.isAlphabetic(content1.charAt(i))) {
                freq1[Character.toLowerCase(content1.charAt(i)) - 'a']++;
            }
        }
        for(int i = 0; i < content2.length(); i++) {
            if(Character.isAlphabetic(content2.charAt(i))) {
                freq2[Character.toLowerCase(content2.charAt(i)) - 'a']++;
            }
        }

        for(int i = 0; i < 26; i++) {
            ret += Math.max(freq1[i], freq2[i]);
        }

        return (int) (((double) ret / (double) Math.min(content1.length(), content2.length())) * 100.0);
    }
}
