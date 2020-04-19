import javax.swing.*;
import java.awt.*;

public class JTetrisBrain extends  JTetris{
    private JCheckBox brainMode;
    private Brain defBrain;
    private Brain.Move move;
    private JSlider  adversary;
    private int cnt;

    JTetrisBrain(int pixels) {
        super(pixels);
        defBrain = new DefaultBrain();
        move = new Brain.Move();
        cnt = -1;
    }

    @Override
    public JComponent createControlPanel() {
        JPanel panel = (JPanel)super.createControlPanel();
        JPanel little = new JPanel();
        little.add(new JLabel("Adversary:"));
        adversary = new JSlider(0, 100, 0);
        adversary.setPreferredSize(new Dimension(100,15)); little.add(adversary);
        brainMode = new JCheckBox("Brain active");
        panel.add(brainMode);
        panel.add(little);
        return panel;
    }

    @Override
    public void tick(int verb) {
        if(brainMode.isSelected() && verb == DOWN){
            if(cnt != count){
                if(currentPiece != null){
                    board.undo();
                    move = defBrain.bestMove(board, currentPiece, board.getHeight(), move);
                }
            }
            cnt = count;
            if(move != null){
                if(!(move.piece).equals(currentPiece)){
                    super.tick(ROTATE);
                }
                else if(currentX > move.x){
                    super.tick(LEFT);
                }
               else if(currentX < move.x) {
                    super.tick(RIGHT);
                }
                if(currentX == move.x && currentY > move.y){
                    super.tick(DROP);
                  // board.commit();
                }
            }
        }
        super.tick(verb);
    }

    @Override
    public Piece pickNextPiece() {
        int randomNumber = random.nextInt(99);
        int value = adversary.getValue();
        if(randomNumber >= value)
            return super.pickNextPiece();
        Piece worstPiece = super.pickNextPiece();
        double worst = 0;
        for(int i = 0; i < pieces.length; i++){
            Piece p = pieces[i];
            board.undo();
            Brain.Move mv = defBrain.bestMove(board, p, board.getHeight(), null);
            if(mv != null && mv.score > worst){
                worstPiece = mv.piece;
                worst = mv.score;
            }
        }
        return worstPiece;

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        JTetrisBrain tetris = new JTetrisBrain(16);
        JFrame frame = JTetris.createFrame(tetris);
        frame.setVisible(true);
    }
}
