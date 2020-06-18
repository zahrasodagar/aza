import java.util.ArrayList;
public abstract class Element {
    static ArrayList<Element> elements=new ArrayList<>();
    String name;
    Nodes[] node=new Nodes[2];
    ArrayList<Double> vs=new ArrayList<>();
    ArrayList<Double> is=new ArrayList<>();
    double integralV=0,integralI=0,dV=0,dI=0;

    public Element(String name,Nodes node1,Nodes node2){
        this.name=name;
        node[0]=node1;
        node[1]=node2;
    }

    public static void update(){ //ghable update nodes
        for (Element element:elements){
            double v=element.node[0].v-element.node[1].v,i=element.getI(element.node[0]);
            double v0=0,i0=0;
            if (element.vs.size()!=0){
                v0=element.vs.get(element.vs.size()-1);
                i0=element.is.get(element.is.size()-1);
            }
            else {
                v0=0;
                i0=0;
            }
            element.vs.add(v);
            element.is.add(i);
            element.dV=(v-v0)/Main.dt;
            element.dI=(i-i0)/Main.dt;
            element.integralV+=v*Main.dt;
            element.integralI+=i*Main.dt;
            if (element instanceof Capacitor)
                ((Capacitor) element).setDC();
            if (element instanceof Inductor)
                ((Inductor) element).setDL();
        }
    }

    public abstract double getI(Nodes thisNode);

    public Nodes otherNode(Nodes thisNode){
        if (thisNode.equals(node[0]))
            return node[1];
        else
            return node[0];
    }

}