package com.genes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Controller {

    // ─── Paleta ───────────────────────────────────────────────────────────────
    private static final String BG_CARD = "-fx-background-color: #161b22;";
    private static final String BG_CARD_HEADER = "-fx-background-color: #1c2128;";
    private static final String BORDER_CARD = "-fx-border-color: #30363d; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;";

    private static final String C_LABEL = "#7d8590";
    private static final String C_VALUE = "#e6edf3";
    private static final String C_SAFE = "#3fb950";
    private static final String C_ALERT = "#f85149";
    private static final String C_WARN = "#d29922";
    private static final String C_INFO = "#58a6ff";
    private static final String C_MUTED = "#484f58";
    private static final String C_ACCENT = "#a5d6ff";

    private static final String FONT = "Consolas";

    // ─── Campos FXML ──────────────────────────────────────────────────────────
    @FXML
    private HBox topBar;
    @FXML
    private TextField campoCaminho;
    @FXML
    private ScrollPane scrollResultado;
    @FXML
    private VBox areaResultado;
    @FXML
    private Label labelStatus;
    @FXML
    private Label labelStatusDot;

    // Variáveis para calcular o movimento da janela

 // --- Variáveis de controle de janela ---
    private double xOffset = 0;
    private double yOffset = 0;

    // --- Constantes de Estilo (Padrão DRY) ---
    private static final String BOTAO_BASE = "-fx-font-size: 20px; -fx-font-family: 'Consolas'; -fx-font-weight: bold; -fx-cursor: hand; -fx-min-width: 40px; -fx-min-height: 30px;";
    
    private static final String MINIMIZAR_NORMAL = "-fx-background-color: transparent; -fx-text-fill: #6b8cba; " + BOTAO_BASE;
    private static final String MINIMIZAR_HOVER  = "-fx-background-color: rgba(107,140,186,0.15); -fx-text-fill: #9ab5d9; " + BOTAO_BASE;
    
    private static final String FECHAR_NORMAL    = "-fx-background-color: transparent; -fx-text-fill: #e8f0fe; " + BOTAO_BASE;
    private static final String FECHAR_HOVER     = "-fx-background-color: #c03a2b4b; -fx-text-fill: white; " + BOTAO_BASE;

    @FXML
    public void initialize() {
        configurarArrastarJanela();
    }

    private void configurarArrastarJanela() {
        topBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        topBar.setOnMouseDragged(e -> {
            Stage stage = getStage(topBar);
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });
    }

    // --- Ações de Janela ---

    @FXML
    private void fecharJanela(ActionEvent event) {
        getStage((Node) event.getSource()).close();
    }

    @FXML
    private void minimizarJanela(ActionEvent event) {
        getStage((Node) event.getSource()).setIconified(true);
    }

    // --- Efeitos de Hover ---

    @FXML 
    private void onMinimizarHover(MouseEvent e) {
        ((Button) e.getSource()).setStyle(MINIMIZAR_HOVER);
    }

    @FXML 
    private void onMinimizarSair(MouseEvent e) {
        ((Button) e.getSource()).setStyle(MINIMIZAR_NORMAL);
    }

    @FXML 
    private void onFecharHover(MouseEvent e) {
        ((Button) e.getSource()).setStyle(FECHAR_HOVER);
    }

    @FXML 
    private void onFecharSair(MouseEvent e) {
        ((Button) e.getSource()).setStyle(FECHAR_NORMAL);
    }

    // --- Métodos Utilitários ---

    private Stage getStage(Node node) {
        return (Stage) node.getScene().getWindow();
    }
    // ─── Acao Principal ───────────────────────────────────────────────────────
    @FXML
    public void escolherArquivo() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select PDF File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        Stage stage = (Stage) campoCaminho.getScene().getWindow();
        File arquivo = fc.showOpenDialog(stage);

        if (arquivo != null) {
            campoCaminho.setText(arquivo.getAbsolutePath());
            areaResultado.getChildren().clear();
            setStatus("Analyzing...", C_WARN);
            analisarPDF(arquivo);
            setStatus("Analysis complete", C_SAFE);
        } else {
            setStatus("No file selected", C_MUTED);
        }
    }

    // ─── Motor Forense ────────────────────────────────────────────────────────
    private void analisarPDF(File arquivo) {
        try (PDDocument doc = PDDocument.load(arquivo)) {

            PDDocumentInformation info = doc.getDocumentInformation();

            // ── [INF] Informacoes do Arquivo ──────────────────────────────────
            VBox s1 = criarSecao("[INF]", "FILE INFORMATION", C_INFO);
            GridPane g1 = criarGrid();
            addRow(g1, 0, "Name", arquivo.getName());
            addRow(g1, 1, "Size", String.format("%.2f KB  (%,d bytes)", arquivo.length() / 1024.0, arquivo.length()));
            addRow(g1, 2, "Path", arquivo.getAbsolutePath());
            addRow(g1, 3, "Pages", String.valueOf(doc.getNumberOfPages()));
            addRow(g1, 4, "PDF Version", String.valueOf(doc.getVersion()));
            addRow(g1, 5, "Encrypted", doc.isEncrypted() ? "YES" : "NO",
                    doc.isEncrypted() ? C_ALERT : C_SAFE);
            s1.getChildren().add(g1);
            areaResultado.getChildren().add(s1);

            // ── [ID] Identidade e Metadados ───────────────────────────────────
            VBox s2 = criarSecao("[ID]", "IDENTITY & METADATA", C_ACCENT);
            GridPane g2 = criarGrid();
            addRow(g2, 0, "Title", verificar(info.getTitle()));
            addRow(g2, 1, "Author", verificar(info.getAuthor()));
            addRow(g2, 2, "Subject", verificar(info.getSubject()));
            addRow(g2, 3, "Keywords", verificar(info.getKeywords()));
            addRow(g2, 4, "Creator Software", verificar(info.getCreator()));
            addRow(g2, 5, "PDF Producer", verificar(info.getProducer()));
            s2.getChildren().add(g2);
            areaResultado.getChildren().add(s2);

            // ── [TIME] Linha do Tempo ─────────────────────────────────────────
            VBox s3 = criarSecao("[TIME]", "TIMELINE", C_INFO);
            GridPane g3 = criarGrid();
            addRow(g3, 0, "Created", formatarData(info.getCreationDate()));
            addRow(g3, 1, "Last Modified", formatarData(info.getModificationDate()));
            if (info.getCreationDate() != null && info.getModificationDate() != null) {
                long diffMs = info.getModificationDate().getTimeInMillis()
                        - info.getCreationDate().getTimeInMillis();
                long diffDias = Math.abs(diffMs) / (1000 * 60 * 60 * 24);
                String delta = diffMs == 0
                        ? "No modifications after creation"
                        : diffDias + " day(s) between creation and last modification";
                addRow(g3, 2, "Delta", delta, diffMs >= 0 ? C_VALUE : C_WARN);
            }
            s3.getChildren().add(g3);
            areaResultado.getChildren().add(s3);

            // ── [PERM] Permissoes de Acesso ───────────────────────────────────
            AccessPermission perm = doc.getCurrentAccessPermission();
            VBox s4 = criarSecao("[PERM]", "ACCESS PERMISSIONS", C_INFO);
            GridPane g4 = criarGrid();
            addRow(g4, 0, "Print", perm.canPrint() ? "Allowed" : "BLOCKED", perm.canPrint() ? C_SAFE : C_ALERT);
            addRow(g4, 1, "Copy Content", perm.canExtractContent() ? "Allowed" : "BLOCKED",
                    perm.canExtractContent() ? C_SAFE : C_ALERT);
            addRow(g4, 2, "Modify", perm.canModify() ? "Allowed" : "BLOCKED", perm.canModify() ? C_SAFE : C_ALERT);
            addRow(g4, 3, "Annotate", perm.canModifyAnnotations() ? "Allowed" : "BLOCKED",
                    perm.canModifyAnnotations() ? C_SAFE : C_ALERT);
            addRow(g4, 4, "Fill Forms", perm.canFillInForm() ? "Allowed" : "BLOCKED",
                    perm.canFillInForm() ? C_SAFE : C_ALERT);
            s4.getChildren().add(g4);
            areaResultado.getChildren().add(s4);

            // ── [THREAT] Vetores de Ataque ────────────────────────────────────
            var names = doc.getDocumentCatalog().getNames();
            boolean temJS = names != null && names.getJavaScript() != null;
            boolean temAnexo = names != null && names.getEmbeddedFiles() != null;
            boolean temAction = doc.getDocumentCatalog().getOpenAction() != null;
            boolean temForm = doc.getDocumentCatalog().getAcroForm() != null;
            boolean temAA = doc.getDocumentCatalog().getCOSObject().containsKey("AA");
            boolean temAssin = !doc.getSignatureDictionaries().isEmpty();

            VBox s5 = criarSecao("[THREAT]", "ATTACK VECTORS & ANOMALIES", C_ALERT);
            GridPane g5 = criarGrid();
            addRow(g5, 0, "Embedded JavaScript", temJS ? "DETECTED  —  Code execution risk" : "CLEAN",
                    temJS ? C_ALERT : C_SAFE);
            addRow(g5, 1, "Embedded Files", temAnexo ? "DETECTED  —  Contains hidden attachments" : "NONE",
                    temAnexo ? C_ALERT : C_SAFE);
            addRow(g5, 2, "Open Action", temAction ? "WARNING  —  Automatic action on open" : "NONE",
                    temAction ? C_WARN : C_SAFE);
            addRow(g5, 3, "Additional Actions (AA)", temAA ? "WARNING  —  Action triggers present" : "NONE",
                    temAA ? C_WARN : C_SAFE);
            addRow(g5, 4, "Interactive Forms", temForm ? "WARNING  —  Contains input fields" : "ABSENT",
                    temForm ? C_WARN : C_SAFE);
            addRow(g5, 5, "Digital Signature", temAssin ? "CERTIFICATE PRESENT" : "ABSENT",
                    temAssin ? C_SAFE : C_MUTED);
            s5.getChildren().add(g5);
            areaResultado.getChildren().add(s5);

            // ── [RISK] Score Forense ──────────────────────────────────────────
            int score = 0;
            if (temJS)
                score += 40;
            if (temAnexo)
                score += 30;
            if (temAction)
                score += 20;
            if (temAA)
                score += 15;
            if (doc.isEncrypted())
                score = Math.min(score + 5, 100);
            areaResultado.getChildren().add(criarScoreCard(score));

            // ── [CONTENT] Preview da Pagina 1 ────────────────────────────────
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(1);
            String texto = stripper.getText(doc).trim();

            VBox s7 = criarSecao("[CONTENT]", "CONTENT  —  PAGE 1", C_INFO);
            if (texto.isEmpty()) {
                Label aviso = makeLabel("No text detected. Possibly an image-based PDF (scanned document).", C_WARN);
                aviso.setWrapText(true);
                aviso.setPadding(new Insets(14, 16, 14, 16));
                s7.getChildren().add(aviso);
            } else {
                GridPane gStats = criarGrid();
                addRow(gStats, 0, "Characters", String.format("%,d", texto.length()));
                addRow(gStats, 1, "Words", String.format("%,d", texto.split("\\s+").length));
                addRow(gStats, 2, "Lines", String.format("%,d", texto.split("\n").length));
                s7.getChildren().add(gStats);

                Label preview = new Label(
                        texto.length() > 500 ? texto.substring(0, 500) + "\n[...excerpt truncated]" : texto);
                preview.setFont(Font.font(FONT, 12));
                preview.setTextFill(Color.web("#c9d1d9"));
                preview.setWrapText(true);
                preview.setStyle(
                        "-fx-background-color: #0d1117;" +
                                "-fx-border-color: #21262d;" +
                                "-fx-border-width: 1;" +
                                "-fx-border-radius: 6;" +
                                "-fx-background-radius: 6;" +
                                "-fx-padding: 12;");
                VBox.setMargin(preview, new Insets(0, 16, 14, 16));
                s7.getChildren().add(preview);
            }
            areaResultado.getChildren().add(s7);

        } catch (InvalidPasswordException e) {
            areaResultado.getChildren().add(
                    criarErroCard("ACCESS DENIED", "The PDF is password-protected and cannot be read."));
            setStatus("Password protected", C_ALERT);
        } catch (IOException e) {
            areaResultado.getChildren().add(
                    criarErroCard("READ ERROR", "Corrupted file or invalid format.\nDetail: " + e.getMessage()));
            setStatus("Error processing file", C_ALERT);
        }
    }

    // ─── Construtores de UI ───────────────────────────────────────────────────

    private VBox criarSecao(String prefixo, String titulo, String corPrefixo) {
        VBox card = new VBox(0);
        card.setStyle(BG_CARD + BORDER_CARD);
        VBox.setMargin(card, new Insets(0, 0, 10, 0));

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
                BG_CARD_HEADER +
                        "-fx-background-radius: 8 8 0 0;" +
                        "-fx-border-color: transparent transparent #21262d transparent;" +
                        "-fx-border-width: 0 0 1 0;" +
                        "-fx-padding: 9 16 9 16;");

        Label lblPrefixo = new Label(prefixo);
        lblPrefixo.setFont(Font.font(FONT, FontWeight.BOLD, 11));
        lblPrefixo.setTextFill(Color.web(corPrefixo));

        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font(FONT, FontWeight.BOLD, 11));
        lblTitulo.setTextFill(Color.web("#8b949e"));

        header.getChildren().addAll(lblPrefixo, lblTitulo);
        card.getChildren().add(header);
        return card;
    }

    private GridPane criarGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(0);
        grid.setPadding(new Insets(4, 16, 4, 16));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(185);
        col1.setPrefWidth(185);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);
        return grid;
    }

    private void addRow(GridPane grid, int row, String chave, String valor) {
        addRow(grid, row, chave, valor, C_VALUE);
    }

    private void addRow(GridPane grid, int row, String chave, String valor, String corValor) {
        if (row > 0) {
            Region sep = new Region();
            sep.setPrefHeight(1);
            sep.setStyle("-fx-background-color: #21262d;");
            GridPane.setColumnSpan(sep, 2);
            grid.add(sep, 0, row * 2 - 1);
        }

        Label lChave = makeLabel(chave, C_LABEL);
        lChave.setPadding(new Insets(8, 0, 8, 0));

        Label lValor = makeLabel(valor, corValor);
        lValor.setPadding(new Insets(8, 0, 8, 0));
        lValor.setWrapText(true);

        grid.add(lChave, 0, row * 2);
        grid.add(lValor, 1, row * 2);
    }

    private VBox criarScoreCard(int score) {
        String cor = score >= 60 ? C_ALERT : score >= 20 ? C_WARN : C_SAFE;
        String nivel = score >= 60 ? "HIGH RISK" : score >= 20 ? "MODERATE RISK" : "LOW RISK";
        String descr = score >= 60
                ? "Multiple indicators of compromise detected. Manual analysis recommended."
                : score >= 20
                        ? "Suspicious elements present. Review alerts before distributing."
                        : "No critical anomalies detected. Document appears to be safe.";

        VBox card = criarSecao("[RISK]", "FORENSIC RISK SCORE", cor);

        HBox corpo = new HBox(24);
        corpo.setPadding(new Insets(16));
        corpo.setAlignment(Pos.CENTER_LEFT);

        Label lblScore = new Label(score + "/100");
        lblScore.setFont(Font.font(FONT, FontWeight.BOLD, 36));
        lblScore.setTextFill(Color.web(cor));

        VBox painel = new VBox(8);
        painel.setAlignment(Pos.CENTER_LEFT);

        Label lblNivel = new Label(nivel);
        lblNivel.setFont(Font.font(FONT, FontWeight.BOLD, 12));
        lblNivel.setTextFill(Color.web(cor));

        HBox barraFundo = new HBox();
        barraFundo.setPrefHeight(5);
        barraFundo.setMaxWidth(Double.MAX_VALUE);
        barraFundo.setStyle("-fx-background-color: #21262d; -fx-background-radius: 3;");

        Region barraFill = new Region();
        barraFill.setPrefHeight(5);
        barraFill.setStyle("-fx-background-color: " + cor + "; -fx-background-radius: 3;");
        HBox.setHgrow(barraFill, Priority.NEVER);
        barraFundo.getChildren().add(barraFill);

        barraFundo.widthProperty()
                .addListener((obs, oldW, newW) -> barraFill.setPrefWidth(newW.doubleValue() * (score / 100.0)));

        Label lblDescr = makeLabel(descr, C_LABEL);
        lblDescr.setWrapText(true);

        painel.getChildren().addAll(lblNivel, barraFundo, lblDescr);
        HBox.setHgrow(painel, Priority.ALWAYS);

        corpo.getChildren().addAll(lblScore, painel);
        card.getChildren().add(corpo);
        VBox.setMargin(card, new Insets(0, 0, 10, 0));
        return card;
    }

    private VBox criarErroCard(String prefixo, String mensagem) {
        VBox card = criarSecao("[ERR]", prefixo, C_ALERT);
        Label msg = makeLabel(mensagem, C_LABEL);
        msg.setWrapText(true);
        msg.setPadding(new Insets(14, 16, 14, 16));
        card.getChildren().add(msg);
        return card;
    }

    private Label makeLabel(String texto, String cor) {
        Label l = new Label(texto);
        l.setFont(Font.font(FONT, 12));
        l.setTextFill(Color.web(cor));
        return l;
    }

    private void setStatus(String texto, String cor) {
        if (labelStatus != null) {
            labelStatus.setText(texto);
            labelStatus.setTextFill(Color.web(cor));
        }
        if (labelStatusDot != null) {
            labelStatusDot.setTextFill(Color.web(cor));
        }
    }

    // ─── Hover do Botao ───────────────────────────────────────────────────────
    @FXML
    public void onBotaoHover(javafx.scene.input.MouseEvent e) {
        ((Button) e.getSource()).setStyle(
                "-fx-background-color: #2d96f0; -fx-text-fill: #ffffff; " +
                        "-fx-font-family: 'Consolas'; -fx-font-size: 12px; -fx-font-weight: bold; " +
                        "-fx-border-radius: 6; -fx-background-radius: 6; " +
                        "-fx-padding: 8 16 8 16; -fx-cursor: hand;");
    }

    @FXML
    public void onBotaoSair(javafx.scene.input.MouseEvent e) {
        ((Button) e.getSource()).setStyle(
                "-fx-background-color: #1e7fd4; -fx-text-fill: #ffffff; " +
                        "-fx-font-family: 'Consolas'; -fx-font-size: 12px; -fx-font-weight: bold; " +
                        "-fx-border-radius: 6; -fx-background-radius: 6; " +
                        "-fx-padding: 8 16 8 16; -fx-cursor: hand;");
    }

    // ─── Utilitarios ──────────────────────────────────────────────────────────
    private String verificar(String dado) {
        return (dado == null || dado.trim().isEmpty()) ? "—" : dado;
    }

    private String formatarData(Calendar data) {
        if (data == null)
            return "—";
        return new SimpleDateFormat("MM/dd/yyyy  HH:mm:ss  (z)").format(data.getTime());
    }
}