/*
 * Componentes principais:
 * - Mostrar e ocultar senha nos campo de digitar senha
 * 
 * initialize()
 * Redimensiona dinamicamente a imagem de fundo conforme a tela usando utilitário `Redimensionamento`.
 * 
 * onBtnLogar(ActionEvent)
 * Fluxo principal de login do usuário:
 * - Recupera e valida credenciais (usuário/senha) via `UsuarioService`.
 * - Verifica se o e-mail está confirmado via `EmailTokenStore`.
 * - Em caso de sucesso:
 *   - Recupera o usuário completo.
 *   - Salva o usuário na sessão (`SessaoUsuario`).
 *   - Carrega a `TelaMenu.fxml` e inicia nova cena com os dados do usuário.
 * - Em caso de falha:
 *   - Exibe alerta de erro com feedback ao usuário.
 * 
 * onBtnCadastro(ActionEvent)
 * Altera a cena atual para a tela de cadastro (`TelaCadastro.fxml`).
 *
 * onBtnEsqueciSenha(ActionEvent)
 * Redireciona para a tela de recuperação de senha via OTP (`TelaOTP.fxml`).
 * 
 * onBtnNovaSessao(ActionEvent)
 * Abre uma nova janela da aplicação com a tela de login.
 *
 * Estruturas e técnicas utilizadas:
 * - Injeção de dependência via Singleton (`UsuarioService`, `SessaoUsuario`).
 * - Navegação entre telas com `FXMLLoader`.
 * - Controle de fluxo baseado em estados do usuário (login e confirmação de e-mail).
 * - `try/catch` para controle de exceções durante carregamento de FXML.
 * - Manipulação de UI via `TextField`, `Button`, `ImageView`, `StackPane`, `AnchorPane`, etc.
 */

package controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Usuario;
import otp.EmailTokenStore;
import service.Alertas;
import service.Redimensionamento;
import service.UsuarioService;
import session.SessaoUsuario;

public class TelaLoginController {

	private UsuarioService usuarioService = UsuarioService.getInstance();

	@FXML private TextField txtUsuarioLogin;

	@FXML private PasswordField txtSenhaLogin;
	@FXML private TextField txtSenhaLoginVisible;
	@FXML private Button btnVerSenhaLogin;
	@FXML private ImageView imgSenhaLogin;

	@FXML private Button btnLogar;
	@FXML private Button btnCadastro;
	@FXML private Button btnEsqueciSenha;
	@FXML private StackPane telaLogin;
	@FXML private AnchorPane contentPane;
	@FXML private Group grupoCampos;
	@FXML private ImageView backgroundImage;

	private final Image mostrarSenha = new Image(getClass().getResource("/resources/btnIcons/MostrarSenha.png").toExternalForm());
	private final Image ocultarSenha = new Image(getClass().getResource("/resources/btnIcons/OcultarSenha.png").toExternalForm());

	/*
	 * initialize()
	 * Redimensiona dinamicamente a imagem de fundo conforme a tela usando utilitário `Redimensionamento`.
	 */
	@FXML
	public void initialize() {
		Redimensionamento.aplicarRedimensionamento(telaLogin, backgroundImage, grupoCampos);
		imgSenhaLogin.setImage(ocultarSenha);
	}

	/*
	 * onBtnLogar(ActionEvent)
	 * Fluxo principal de login do usuário:
	 * - Recupera e valida credenciais (usuário/senha) via `UsuarioService`.
	 * - Verifica se o e-mail está confirmado via `EmailTokenStore`.
	 * - Em caso de sucesso:
	 *   - Recupera o usuário completo.
	 *   - Salva o usuário na sessão (`SessaoUsuario`).
	 *   - Carrega a `TelaMenu.fxml` e inicia nova cena com os dados do usuário.
	 * - Em caso de falha:
	 *   - Exibe alerta de erro com feedback ao usuário.
	 */
	@FXML
	private void onBtnLogar(ActionEvent event) {
		Alertas a = new Alertas();
		String email = txtUsuarioLogin.getText();
		String senha = txtSenhaLogin.isVisible() ? txtSenhaLogin.getText() : txtSenhaLoginVisible.getText();

		if (usuarioService.fazerLogin(email, senha)) {
			if (!EmailTokenStore.isEmailConfirmed(email)) {
				a.mostrarAlerta("Acesso negado", "Você precisa confirmar seu e-mail antes de acessar.");
				return;
			}

			try {
				Usuario usuario = usuarioService.getUsuarioPorEmail(email);
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaMenu.fxml"));
				Parent root = loader.load();

				TelaMenuController controller = loader.getController();
				controller.setUsuarioLogado(usuario);
				SessaoUsuario.getInstance().setUsuario(usuario);

				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			a.mostrarAlerta("Erro!!", "Senha incorreta ou email inexistente");
		}
	}

	/*
	 * onBtnCadastro(ActionEvent)
	 * Altera a cena atual para a tela de cadastro (`TelaCadastro.fxml`).
	 */
	@FXML
	private void onBtnCadastro(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCadastro.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * onBtnEsqueciSenha(ActionEvent)
	 * Redireciona para a tela de recuperação de senha via OTP (`TelaOTP.fxml`).
	 */
	@FXML
	private void onBtnEsqueciSenha(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaOTP.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * onBtnNovaSessao(ActionEvent)
	 * Abre uma nova janela da aplicação com a tela de login.
	 */
	@FXML
	private void onBtnNovaSessao(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaLogin.fxml"));
			Parent root = loader.load();

			Stage novaJanela = new Stage();
			novaJanela.setTitle("EvenMoreFun");
			novaJanela.setScene(new Scene(root));
			novaJanela.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * toggleSenhaLoginVisibility()
	 * Alterna a visibilidade do campo de senha na tela de login.
	 */
	@FXML
	private void toggleSenhaLoginVisibility() {
		if (txtSenhaLoginVisible.isVisible()) {
			txtSenhaLogin.setText(txtSenhaLoginVisible.getText());
			txtSenhaLoginVisible.setVisible(false);
			txtSenhaLoginVisible.setManaged(false);
			txtSenhaLogin.setVisible(true);
			txtSenhaLogin.setManaged(true);
			imgSenhaLogin.setImage(ocultarSenha);
		} else {
			txtSenhaLoginVisible.setText(txtSenhaLogin.getText());
			txtSenhaLogin.setVisible(false);
			txtSenhaLogin.setManaged(false);
			txtSenhaLoginVisible.setVisible(true);
			txtSenhaLoginVisible.setManaged(true);
			imgSenhaLogin.setImage(mostrarSenha);
		}
	}
}
