//package awele.bot.competitor.aweleBOT;
//
//import awele.core.Board;
//
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//
//public class Node {
//    public Node parentNode;
//    public ArrayList<Node> childNodes;
//
//    public int simulationCount;
//    public int simulationWins;
//
//    public int visitCount;
//
//    public int holeIndex;
//
//    public int hole;
//
//    public double constant = /*Math.sqrt(2)*/2;
//
//    public Node(Node parent, ArrayList<Node> children){
//        parentNode = parent;
//        childNodes = children;
//        simulationCount = 0;
//        simulationWins = 0;
//        visitCount = 0;
//    }
//
//    public Node getParentNode(){
//        return parentNode;
//    }
//
//    public ArrayList<Node> getChildNodes(){
//        return childNodes;
//    }
//
//    public void setChildNodes(ArrayList<Node> children){
//        childNodes = children;
//    }
//    public int getSimulationCount(){
//        return simulationCount;
//    }
//
//    public int getSimulationWins(){
//        return simulationWins;
//    }
//
//    public int getHoleIndex(){
//        return holeIndex;
//    }
//
//    public void setSimulationCount(int number){
//        simulationCount = number;
//    }
//
//    /*public Node getBestFollowingNode(){
//        if(isLeafNode()) return null;
//        Node bestNode = childNodes.get(0);
//        double bestNodeValue = UCT(childNodes.get(0).getSimulationWins(),childNodes.get(0).getSimulationCount(),getSimulationCount(),Math.sqrt(2));
//        for(int i = 0; i < childNodes.size(); i++){
//            double currentUCT = UCT(childNodes.get(i).getSimulationWins(),childNodes.get(i).getSimulationCount(),getSimulationCount(),Math.sqrt(2));
//            if(currentUCT > bestNodeValue){
//                bestNode = childNodes.get(i);
//                bestNodeValue = UCT(childNodes.get(i).getSimulationWins(),childNodes.get(i).getSimulationCount(),getSimulationCount(),Math.sqrt(2));
//            }
//        }
//        return bestNode;
//    }*/
//    public Node getBestFollowingNode(){
//        if(isLeafNode()) return this;
//        int k = 0;
//        Node bestNode = childNodes.get(0);
//        double bestNodeValue = UCT(childNodes.get(0).getSimulationWins(),childNodes.get(0).getSimulationCount(),getSimulationCount(),constant);
//        for(int i = 0; i < childNodes.size(); i++){
//            double currentUCT = UCT(childNodes.get(i).getSimulationWins(),childNodes.get(i).getSimulationCount(),getSimulationCount(),constant);
//            if(currentUCT > bestNodeValue){
//                bestNode = childNodes.get(i);
//                bestNodeValue = UCT(childNodes.get(i).getSimulationWins(),childNodes.get(i).getSimulationCount(),getSimulationCount(),constant);
//                k = i;
//            }
//        }
//        //System.out.println("BEST NODE IS "+k);
//        return bestNode.getBestFollowingNode();
//    }
//    public double getUCT(){
//        return UCT(simulationWins,simulationCount, parentNode.simulationCount,constant);
//    }
//    public Node getMostWinningFollowingNode(){
//        if(isLeafNode()) return this;
//        Node bestNode = childNodes.get(5);
//        double bestNodeValue = (double)bestNode.getSimulationWins()/(double)bestNode.getSimulationCount();
//        for(int i = 0; i < childNodes.size(); i++){
//            double currentUCT = (double)childNodes.get(i).getSimulationWins()/(double)childNodes.get(i).getSimulationCount();
//            if(currentUCT > bestNodeValue || (currentUCT == bestNodeValue && childNodes.get(i).getSimulationWins()>bestNode.getSimulationWins())){
//                bestNode = childNodes.get(i);
//                bestNodeValue = (double)childNodes.get(i).getSimulationWins()/(double)childNodes.get(i).getSimulationCount();
//            }
//        }
//        return bestNode;
//    }
//    public double[] getUCTPercentages(){
//        double[] percentages = {0,0,0,0,0,0};
//        if(childNodes == null) return percentages;
//        for(int i = 0; i < childNodes.size(); i++){
//            if(childNodes.get(i).getSimulationCount() == 0) percentages[childNodes.get(i).hole] = 0;
//            else percentages[childNodes.get(i).hole] = childNodes.get(i).getUCT();
//        }
//        return percentages;
//    }
//    public double[] getWinningPercentages(){
//        double[] percentages = {0,0,0,0,0,0};
//        if(childNodes == null) return percentages;
//        for(int i = 0; i < childNodes.size(); i++){
//            if(childNodes.get(i).getSimulationCount() == 0) percentages[childNodes.get(i).hole] = 0;
//            else percentages[childNodes.get(i).hole] = (double)childNodes.get(i).getSimulationWins()/(double)childNodes.get(i).getSimulationCount();
//        }
//        return percentages;
//    }
//    public boolean isLeafNode(){
//        return (childNodes == null) || (childNodes.size() == 0);
//    }
//    public double UCT(int w, int n, int N, double c){
//        //System.out.println("UCTTTTTTT: " + (((double)w / (double)n) + c * (Math.sqrt(Math.log((double)N)/(double)n))));
//        if(n == 0) return Double.MAX_VALUE;
//        /*System.out.println(w+" "+n+" "+N+" "+c);
//        System.out.println(((double)w / (double)n) );
//        System.out.println(Math.log(N)/(double)n);
//        System.out.println((Math.sqrt(Math.log(N)/(double)n)));
//        System.out.println( c * (Math.sqrt(Math.log(N)/(double)n)));
//        System.out.println(((double)w / (double)n) + c * (Math.sqrt(Math.log(N)/(double)n)));*/
//
//        return (((double)w / (double)n) + c * (Math.sqrt(Math.log((double)N)/(double)n)));
//    }
//
//    public boolean alreadyVisited(){
//        return simulationCount != 0;
//    }
//    public void setSimulationWins(int number){
//        simulationWins = number;
//    }
//
//    public void increaseSimulationCount(){
//        simulationCount += 1;
//    }
//
//    public void increaseSimulationWins(){
//        simulationWins += 1;
//    }
//
//    public void printNode(){
//        String percentage = "";
//        if(getSimulationCount() == 0) percentage = "-1000";
//        else percentage = Double.toString((double)getSimulationWins()/(double)getSimulationCount());
//        System.out.println(getSimulationWins() + "/" + getSimulationCount() + " : " +  percentage +  "%");
//    }
//
//}
