package CodingRaptors;


import java.awt.*;
import java.io.Serializable;

public class CPolygon implements Comparable, Serializable {

    private Polygon myPol;
    private static final long serialVersionUID = 11L;

    public CPolygon(Polygon pol) {
        this.myPol = pol;
    }

    public int compareTo(Object o) {
        if (o instanceof CPolygon) {
            double polArea = Math.abs(((CPolygon) o).myPol.getBounds().getSize().getHeight() * ((CPolygon) o).myPol.getBounds().getSize().getWidth());
            double myArea = Math.abs(myPol.getBounds().getSize().getHeight() * myPol.getBounds().getSize().getWidth());
            if (myArea < polArea) {
                return -1;
            } else if (myArea > polArea)
                return 1;
        } else {
            Polygon pol = (Polygon) o;
            double polArea = Math.abs(pol.getBounds().getSize().getHeight() * pol.getBounds().getSize().getWidth());
            double myArea = Math.abs(myPol.getBounds().getSize().getHeight() * myPol.getBounds().getSize().getWidth());
            if (myArea < polArea)
                return -1;
            else if (myArea > polArea)
                return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "" + Math.abs(myPol.getBounds().getSize().getHeight() * myPol.getBounds().getSize().getWidth());
    }

    public String coordinatesToString() {
        int[] x = myPol.xpoints;
        int[] y = myPol.ypoints;
        StringBuilder sb = new StringBuilder();
        sb.append("x coordinates: " );
        for(int singleCoordinate: x) {
            sb.append(singleCoordinate + " ");
        }
        sb.append("\n");

        sb.append("y coordinates: " );
        for(int singleCoordinate: y) {
            sb.append(singleCoordinate + " ");
        }
        sb.append("\n");
        return sb.toString();
    }

    public boolean equals(CPolygon o) {
        double polArea = Math.abs(o.myPol.getBounds().getSize().getHeight() * o.myPol.getBounds().getSize().getWidth());
        double myArea = Math.abs(myPol.getBounds().getSize().getHeight() * myPol.getBounds().getSize().getWidth());
        if (polArea == myArea)
            return true;
        return false;
    }

    public static boolean sameCPol(CPolygon a, CPolygon b) {
        int[] xa = a.myPol.xpoints, xb = b.myPol.xpoints;
        int[] ya = a.myPol.ypoints, yb = b.myPol.ypoints;
        int apoints = a.myPol.npoints, bpoints = b.myPol.npoints;
        Polygon ap = new Polygon(xa, ya, apoints), bp = new Polygon(xb, yb, bpoints);
        return DBApp.samePol(ap, bp);
    }
    public boolean equal(CPolygon p2){
        int[] p1x = getMyPol().xpoints;
        int[] p2x = p2.getMyPol().xpoints;
        int[] p1y = getMyPol().ypoints;
        int[] p2y = p2.getMyPol().ypoints;
        if(p1x.length != p2x.length)
            return false;
        for(int i = 0; i<p1x.length; i++){
            boolean flag = true;
            for(int j = 0; j<p2x.length; j++){
                if(p1x[i] == p2x[j]) {
                    if (p1y[i] != p2y[j])
                        flag = false;
                    else {flag = true; break;}
                }
                if(j == p2x.length-1)
                    flag = false;
            }
            if(!flag)
                return false;
        }
        return true;
    }

    public Polygon getMyPol() {
        return this.myPol;
    }

    public static void main(String[] args) {
//

        Polygon p1 = new Polygon(new int[]{1, 1, 2, 2}, new int[]{1, 2, 1, 2}, 4);
        CPolygon c1 = new CPolygon(p1);

        Polygon p2 = new Polygon(new int[]{100, 100, 200, 200}, new int[]{100, 500, 100, 500}, 4);
        CPolygon c2 = new CPolygon(p2);

        Polygon p3 = new Polygon(new int[]{50, 50, 20, 20}, new int[]{10, 50, 10, 50}, 4);
        CPolygon c3 = new CPolygon(p3);

        Polygon p4 = new Polygon(new int[]{25, 25, 50, 50}, new int[]{10, 25, 10, 25}, 4);
        CPolygon c4 = new CPolygon(p4);


        Polygon p5 = new Polygon(new int[]{205, 205, 405, 405}, new int[]{300, 500, 300, 500}, 4);
        CPolygon c5 = new CPolygon(p5);


        Polygon p6 = new Polygon(new int[]{1, 1, 2, 2}, new int[]{60, 90, 60, 90}, 4);
        CPolygon c6 = new CPolygon(p6);

        Polygon p7 = new Polygon(new int[]{1, 1, 2, 2}, new int[]{20, 50, 20, 50}, 4);
        CPolygon c7 = new CPolygon(p7);


        Polygon p8 = new Polygon(new int[]{50, 50, 20, 20}, new int[]{10, 50, 10, 50}, 4);
        CPolygon c8 = new CPolygon(p8);
//////
//////
//        Polygon p3=new Polygon (new int[]{1, 1, 3, 3},new int[]{1,6,1,6},4);
//        CPolygon c3=new CPolygon(p3);
//        Polygon p4=new Polygon (new int[]{1, 1, 4, 4},new int[]{1,7,1,7},4);
//        CPolygon c4=new CPolygon(p4);
        System.out.println("c1 area= " + Math.abs(c1.myPol.getBounds().getSize().getHeight() * c1.myPol.getBounds().getSize().getWidth()));
        System.out.println("c2 area= " + Math.abs(c2.myPol.getBounds().getSize().getHeight() * c2.myPol.getBounds().getSize().getWidth()));
        System.out.println("c3 area= " + Math.abs(c3.myPol.getBounds().getSize().getHeight() * c3.myPol.getBounds().getSize().getWidth()));
        System.out.println("c4 area= " + Math.abs(c4.myPol.getBounds().getSize().getHeight() * c4.myPol.getBounds().getSize().getWidth()));
        System.out.println("c5 area= " + Math.abs(c5.myPol.getBounds().getSize().getHeight() * c5.myPol.getBounds().getSize().getWidth()));
        System.out.println("c6 area= " + Math.abs(c6.myPol.getBounds().getSize().getHeight() * c6.myPol.getBounds().getSize().getWidth()));
        System.out.println("c7 area= " + Math.abs(c7.myPol.getBounds().getSize().getHeight() * c7.myPol.getBounds().getSize().getWidth()));
        System.out.println("c8 area= " + Math.abs(c8.myPol.getBounds().getSize().getHeight() * c8.myPol.getBounds().getSize().getWidth()));
//
//      System.out.print(c5.compareTo(c1));
    }


}
