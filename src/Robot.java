import BinCode.BinCode;
import com.sun.javafx.geom.Vec2f;
import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class Robot {
    private ArrayList<Vec2f> speeds;
    private ArrayList<Vec2f> forces;
    protected ArrayList<Point2D> location_arr;
    private Point2D location;
    private float F_m;
    private float dt;
    protected int moves;
    private float rad;
    protected int ind;
    protected boolean ifElite;
    ArrayList<Pair<Point2D, Float>> noise_arr;
    protected int[] genes;
    protected int offset;
    protected float penalty;
    protected int[] penalty_ind;
    Stack<Point2D> true_path;
    protected int chromoLength;
    protected float eps;
    public static final ActionType DEFAULT_ACTION_TYPE = ActionType.FOLLOW;

    public enum ActionType {
        RUN, FOLLOW;
    }


    public Robot(ArrayList<Pair<Point2D, Float>> noise, Stack<Point2D> tr_path, float Fm, float dt, boolean ifElite) {
        speeds = new ArrayList<>();
        speeds.add(new Vec2f(0, 0));
        forces = new ArrayList<>();
        noise_arr = noise;
        true_path = (Stack<Point2D>) tr_path.clone();
        location = new Point2D.Float(0, 0);
        this.ifElite = ifElite;
        if (ifElite) {
            penalty_ind = new int[2001];

        }
        location_arr = new ArrayList<>();
        moves = 0;
        offset = 0;
        F_m = Fm;
        this.dt = dt;
        penalty = 0;
        eps = dt * dt * dt;
    }

    public int[] setGene(int[] chromo) {
        genes = new int[chromoLength];

        for (int i = chromoLength - chromo.length; i < chromoLength; i++) {
            for (int j = 0; j < chromo.length; j++)
                genes[i] = chromo[j];
        }
        return genes;
    }

    public void startShow(float Fmdt) {
        rad = Fmdt;
        ind = 0;
        int clearRun = 0;
        float U_x_final = 0, U_y_final = 0;
        boolean U_final = false, F_final = false;
        location_arr.add(new Point2D.Float(0, 0));
        while (!(U_final && F_final)) {
            int k = checkForPosibRoute(speeds.get(ind), rad);
            if (U_final)
                F_final = true;
            if (k == true_path.size() - 1) {
                U_final = true;
            }
            if (k >= 0) {
                if (speeds.size() != 0 && ind <= forces.size() - 1) {
                    forces.set(ind, new Vec2f((float) ((1 / dt) * (((true_path.get(k).getX() - location.getX()) / dt) - speeds.get(ind).x)),
                            (float) ((1 / dt) * (((true_path.get(k).getY() - location.getY()) / dt) - speeds.get(ind).y))));
                } else
                    forces.add(new Vec2f((float) ((1 / dt) * (((true_path.get(k).getX() - location.getX()) / dt) - speeds.get(ind).x)),
                            (float) ((1 / dt) * (((true_path.get(k).getY() - location.getY()) / dt) - speeds.get(ind).y))));

                //    System.out.println("New acclereation is: "+ f_k);
                clearRun++;
            } else {
                clearRun = 0;
                U_final = false;
                F_final = false;
                //был штраф, те были ли мы здесь?
                if (penalty_ind[ind] != 0) {
                    //пока можно уменьшить
                    if (k - penalty_ind[ind] >= 0) {
                        double x = true_path.get(k - penalty_ind[ind]).getX();
                        double y = true_path.get(k - penalty_ind[ind]).getY();
                        if ((x - y) * (x - y) <= Fmdt * Fmdt) {
                            penalty_ind[ind] -= 3;
                            continue;
                        }
                    } else {
                        penalty_ind[ind] = 0;
                        if (ind != 0)
                            penalty_ind[ind - 1] -= 3;
                        undoPos(ind);
                        ind--;
                    }
                } else {
                    penalty_ind[ind] = 0;
                    if (ind != 0)
                        penalty_ind[ind - 1] -= 3;
                    else penalty_ind[ind] -= 3;
                    undoPos(ind);
                    ind--;
                }

                continue;
            }

            if (speeds.size() != 0 && ind + 1 < speeds.size())
                speeds.set(ind + 1, new Vec2f((forces.get(ind).x * dt + speeds.get(ind).x), (forces.get(ind).y * dt + speeds.get(ind).y)));
            else {
                speeds.add(new Vec2f((forces.get(ind).x * dt + speeds.get(ind).x), (forces.get(ind).y * dt + speeds.get(ind).y)));
            }


            //      System.out.println("Speed is: "+U);
            setPos(ind);
            Point2D p = new Point2D.Float((float) location.getX(), (float) location.getY());
            if ((ind + 1 < location_arr.size())) {
                location_arr.set(ind + 1, p);
            } else location_arr.add(p);
            if (checkLineConnection(location)) {
//                System.out.println("DEAD!");
                clearRun = 0;
                U_final = false;
                F_final = false;

                //if this not a first step
                undoPosIfCrash(ind);
                if (penalty_ind[ind] != 0) {
                    //пока можно уменьшить
                    if (k - penalty_ind[ind] >= 0) {
                        penalty_ind[ind] -= 3;
                        continue;
                    } else {

                        if (ind != 0)
                            penalty_ind[ind - 1] -= 3;
                        undoPos(ind);
                        ind--;
                    }
                } else {
                    penalty_ind[ind] = 0;
                    if (ind != 0)
                        penalty_ind[ind - 1] -= 3;
                    else penalty_ind[ind] -= 3;
                    undoPos(ind);
                    ind--;
                }
//                if (ind!=0 ) {
//                    penalty_ind[ind - 1] += -15;
//                }
//                else if(ind==0) {
//                    penalty_ind[ind] += -15;
//                    continue;
//                }
//                ind--;
//                 if(ind!=0&&penalty_ind[ind]==0) {

                continue;
            }

            //    System.out.println("Pos is: "+ pos);

            moves++;
            ind++;

        }

    }

    public Robot run(float Fmdt) {
        location_arr.add(new Point2D.Float(0, 0));
        while (((1 - location.getX() >= eps) && (1 - location.getY() >= eps)) && (offset + 64 < chromoLength || offset != chromoLength)) {
            makeNextAction();
            ind++;
            if (checkCollision(location) || location.getY() < 0 || location.getY() < 0 || location.getX() > 1 || location.getY() > 1) {
                //   System.out.println("DEAD!");
                penalty += 100;
                return this;
            }
            location_arr.add(location);
        }
        return this;
    }

    public void makeNextAction() {
        StringBuilder builder = new StringBuilder();
        Vec2f U = new Vec2f(speeds.get(ind));
        if (offset == chromoLength - 64) {
            for (int i = offset; i < chromoLength; i++) {
                builder.append(genes[i]);
            }
        } else {
            for (int i = offset; i < offset + 64; i++) {
                builder.append(genes[i]);
            }
        }
        offset += 64;
        Vec2f f = BinCode.convertBinToVec2f(builder.toString());
//        if(f.y==0&&f.x==0){
//            ind--;
//            return;
//        }
        forces.add(ind, f);
        speeds.add(new Vec2f((forces.get(ind).x * dt + U.x), (forces.get(ind).y * dt + U.y)));

        setPos(ind);
        location_arr.add(location);
        moves++;
    }

    public Point2D showLocation() {
        return location;
    }


    public boolean checkCollision(Point2D p) {
        for (int i = 0; i < noise_arr.size(); i++) {
            double R = noise_arr.get(i).getValue();
            double dx = Math.abs(p.getX() - noise_arr.get(i).getKey().getX());
            if (dx > R)
                return false;
            double dy = Math.abs(p.getY() - noise_arr.get(i).getKey().getY());
            if (dy > R)
                return false;
            if (dx + dy <= R)
                return true;
            return (dx * dx + dy * dy <= R * R);

        }
        return false;
    }

    public boolean checkLineConnection(Point2D current) {
        if (location_arr.size() < 1)
            return false;
        float a, b, c;
        float x1, y1;
        if (ind == 0) {
            a = (float) (location_arr.get(ind).getY() - current.getY());
            x1 = (float) location_arr.get(ind).getX();
            y1 = (float) location_arr.get(ind).getY();
            b = (float) (current.getX() - location_arr.get(ind).getX());
            c = (float) (location_arr.get(ind).getX() * current.getY() - current.getX() * location_arr.get(ind).getY());
        } else {
            a = (float) (location_arr.get(ind - 1).getY() - current.getY());
            x1 = (float) (location_arr.get(ind - 1).getX());
            y1 = (float) (location_arr.get(ind - 1).getY());
            b = (float) (current.getX() - location_arr.get(ind - 1).getX());
            c = (float) (location_arr.get(ind - 1).getX() * current.getY() - current.getX() * location_arr.get(ind - 1).getY());
        }

        for (int i = 0; i < noise_arr.size(); i++) {
            float d = (float) (Math.abs((float) (a * noise_arr.get(i).getKey().getX() + b * noise_arr.get(i).getKey().getY() + c)) / Math.sqrt(a * a + b * b));
            if (d <= noise_arr.get(i).getValue()) {
                if (x1 <= noise_arr.get(i).getKey().getX() && noise_arr.get(i).getKey().getX() <= current.getX())
                    return true;
            }
        }
        return false;

    }

    public float dist(Point2D target) {
        return (float) Math.abs(Math.sqrt((location.getX() - target.getX()) * (location.getX() - target.getX()) + (location.getY() - target.getY()) * (location.getY() - target.getY())));
    }

    //TODO FITNESS!
    public float distOverMoves(Point2D target) {
//        return moves-penalty-dist(new Point2D.Float(0,0))*100;

        return ((float) (0.3f * moves - 0.3f * penalty) / (0.3f * dist(target) + 0.1f * dist(new Point2D.Float(0, 0)))); //+ Math.log10(moves)*0.6f));
        //    return  ((float) 1/(0.3f*dist(target)+0.3f*dist(new Point2D.Float(0,0)))-0.3f*moves - 0.3f*penalty); //+ Math.log10(moves)*0.6f));
        //      return  ((float) 1/(0.1f*dist(new Point2D.Float(0,0))+0.3f*dist(target) -  0.5f*moves) - penalty ); //+ Math.log10(moves)*0.6f));
    }


    public int checkForPosibRoute(Vec2f U, float r) {
        int k = -1;
        //todo think about accuracy
        if ((1 - location.getX() <= eps) && (1 - location.getY() <= eps)) {
            if ((-U.x * dt) * (-U.x * dt) + (-U.y * dt) * (-U.y * dt) <= r * r) {
                return true_path.size() - 1 + penalty_ind[ind];
            } else return k;
        }

        //   float r_n=F_m*dt*dt;
        for (int i = 0; i < true_path.size(); i++) {
            Point2D p = true_path.get(i);
            if ((p.getX() - location.getX() - U.x * dt) * (p.getX() - location.getX() - U.x * dt) + (p.getY() - location.getY() - U.y * dt) * (p.getY() - location.getY() - U.y * dt) <= r * r) {
                k = i;
            }
        }
        return k + penalty_ind[ind];
    }

    public void setPos(int index) {
        float x = (float) (location.getX() + dt * speeds.get(index + 1).x);
        float y = (float) (location.getY() + dt * speeds.get(index + 1).y);
        location.setLocation(x, y);
    }

    public void undoPos(int index) {
        float x = (float) (location.getX() - dt * speeds.get(index).x);
        float y = (float) (location.getY() - dt * speeds.get(index).y);
        location.setLocation(x, y);
    }

    public void undoPosIfCrash(int ind) {
        float x = (float) (location.getX() - dt * speeds.get(ind + 1).x);
        float y = (float) (location.getY() - dt * speeds.get(ind + 1).y);
        location.setLocation(x, y);
    }


    public void setChromoLength(int length) {
        chromoLength = length;
        genes = new int[chromoLength];
    }


    protected ArrayList<Point2D> getThisPath() {
        return location_arr;
    }

    public int[] getEliteGene() {
        String gene;
        int k = 0;
        int length = 64 * forces.size();
        genes = new int[length];
        for (int i = 0; i < ind; i++) {
            Vec2f f = forces.get(i);
            gene = BinCode.convertVector2fToBin(f);
            for (int j = 0; j < gene.length(); j++, k++)
                genes[k] = Character.getNumericValue(gene.charAt(j));
        }

        return genes;
    }

    public int[] getGenes() {
        StringBuilder builer = new StringBuilder();
        for (Vec2f f : forces) {
            builer.append(BinCode.convertVector2fToBin(f));
        }
        String result = builer.toString();
        for (int i = result.length(); i < chromoLength; i++) {
            builer.append(0);
        }
        result = builer.toString();


        for (int i = 0; i < result.length(); i++) {
            genes[i] = Character.getNumericValue(result.charAt(i));
        }
        return genes;
    }

    protected void writeData() {
        try (FileWriter writer = new FileWriter("force.txt", false)) {
            writer.write("Forces\nMax is: " + F_m + "\n");
            for (int i = 0; i < ind; i++) {
                Vec2f f = forces.get(i);
                writer.write(f.x + " " + f.y + "\n");
            }


        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
        try (FileWriter writer = new FileWriter("coords.txt", false)) {
            writer.write("Coords\nTotal are " + ind + "\n");
            for (int i = 0; i < ind; i++) {
                Point2D p = location_arr.get(i);
                writer.write(p.getX() + " " + p.getY() + "\n");
            }


        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
    }


}
