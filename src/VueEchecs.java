import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Observable;

public class VueEchecs extends Vue {

    private static PlateauEchecs plateau = new PlateauEchecs();
    private JeuEchecs jeuEchecs;

    public VueEchecs() {
        super(plateau, new JeuEchecs((PlateauEchecs) plateau));  // Appel du constructeur parent d'abord

        this.jeuEchecs = (JeuEchecs) super.getJeu();  // récupère correctement l'objet JeuEchecs

        getFrame().setTitle("Échecs");
        plateau.addObserver(this);

        this.plateau.initialiserGrille();


        // TEST
        test = new DemoPlateau(plateau);

        //test.demoPromotion();
        //test.demoEchec();
        //test.demoEchecEtMat();
        //test.demoPriseEnPassant();
        //test.demoRoque();
        //test.demoPat();
        //test.demoCoupIllegal();
        //test.demoCoupIllegal2();
    }

    @Override
    public void update(Observable o, Object arg) {

        if (arg instanceof String) {
            switch ((String) arg) {
                case "rafraichissement":
                    rafraichirGrille();
                    break;
                case "couleur":
                    colorierSelection();
                    break;
                case "vider":
                    viderSelection();
                    break;

                case "promotion":
                    if (jeuEchecs.estPromotionEnAttente()) {
                        String[] options = {"Reine", "Tour", "Fou", "Cavalier"};
                        int choix = JOptionPane.showOptionDialog(
                                null,
                                "Choisissez la pièce de promotion :",
                                "Promotion",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[0]
                        );

                        jeuEchecs.traiterPromotion(options[choix]);
                    }

                case "accessibilite":
                    afficherCasesAccessibles();
                    break;
                case "viderCA":
                    viderCasesAccessibles();
                    break;

                default:
                    System.out.println("Type de mise à jour inconnu : " + arg);
            }
        }
    }

    public void colorierSelection() {
        if (jeuEchecs != null && jeuEchecs.getCaseSelectionnee() != null) {
            Position pos = jeuEchecs.getCaseSelectionnee();
            labels[pos.x][pos.y].setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
        }
    }

    public void viderSelection() {
        if (jeuEchecs.getCaseSelectionnee() != null) {
            Position pos = jeuEchecs.getCaseSelectionnee();
            labels[pos.x][pos.y].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        }
    }

    public void afficherCasesAccessibles() {
        if (jeuEchecs.getCaseSelectionnee() != null) {
            Position caseSelectionnee = jeuEchecs.getCaseSelectionnee();
            Piece piece = plateau.getPiece(caseSelectionnee.x, caseSelectionnee.y);

            if (piece != null) {
                List<Position> deplacementsPossibles = piece.getDeplacementsPossibles(plateau, caseSelectionnee);

                for (Position p : deplacementsPossibles) {
                    if (p.x >= 0 && p.x < plateau.getAxeX() && p.y >= 0 && p.y < plateau.getAxeY()) {
                        labels[p.x][p.y].repaint();  // Demande de redessiner la case
                    }
                }
            }
        }
    }

    public void viderCasesAccessibles() {
        for (int i = 0; i < plateau.getAxeX(); i++) {
            for (int j = 0; j < plateau.getAxeY(); j++) {
                labels[i][j].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                labels[i][j].repaint();
            }
        }
    }

    @Override
    // Méthode pour redessiner les cases avec des cercles ou des filtres
    protected void paintCase(Graphics g, int x, int y) {
        if (jeu == null || jeuEchecs.getCaseSelectionnee() == null) {
            return;
        }

        Position caseSelectionnee = jeuEchecs.getCaseSelectionnee();

        if (x == caseSelectionnee.x && y == caseSelectionnee.y) {
            return;
        }

        if (plateau.estOccupe(new Position(x, y))) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0, 0, 255, 80));
            g2.fillRect(0, 0, labels[x][y].getWidth(), labels[x][y].getHeight());
            g2.dispose();
        } else {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.BLUE);
            int diameter = Math.min(labels[x][y].getWidth(), labels[x][y].getHeight()) / 4;
            int cx = (labels[x][y].getWidth() - diameter) / 2;
            int cy = (labels[x][y].getHeight() - diameter) / 2;
            g2.fillOval(cx, cy, diameter, diameter);
            g2.dispose();
        }
    }



    @Override
    public void rafraichirGrille() {
        for (int i = 0; i < plateau.getAxeX(); i++) {
            for (int j = 0; j < plateau.getAxeY(); j++) {
                Piece p = plateau.getPiece(i, j);
                labels[i][j].setIcon(null);
                if (p != null && p.getTypePiece() != null) {
                    String chemin = switch (p.getTypePiece()) {
                        case Roi -> p.estBlanche() ? "img/roi_blanc.png" : "img/roi_noir.png";
                        case Reine -> p.estBlanche() ? "img/reine_blanc.png" : "img/reine_noir.png";
                        case Fou -> p.estBlanche() ? "img/fou_blanc.png" : "img/fou_noir.png";
                        case Cavalier -> p.estBlanche() ? "img/cavalier_blanc.png" : "img/cavalier_noir.png";
                        case Tour -> p.estBlanche() ? "img/tour_blanc.png" : "img/tour_noir.png";
                        case Pion -> p.estBlanche() ? "img/pion_blanc.png" : "img/pion_noir.png";
                        default -> null;
                    };
                    if (chemin != null) {
                        ImageIcon icon = new ImageIcon(chemin);
                        Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                        labels[i][j].setIcon(new ImageIcon(img));
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new VueEchecs();
    }
}