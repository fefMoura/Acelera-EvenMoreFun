/*
 * Célula personalizada para exibição de itens de Evento em componentes ListView do JavaFX.
 * 
 * Responsabilidades principais:
 * - Renderização customizada de itens de Evento em listas
 * - Controle do layout e aparência dos itens
 * - Gerenciamento do estado de células vazias
 * 
 * Padrões utilizados:
 * - Custom Cell Factory (padrão do JavaFX)
 * - Lazy loading de componentes
 */

package controllers;

import java.time.format.DateTimeFormatter;

import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Evento;

public class EventoListCell extends ListCell<Evento> {
	@Override
	protected void updateItem(Evento evento, boolean empty) {
		super.updateItem(evento, empty);

		if (empty || evento == null) {
			setText(null);
			setGraphic(null);
		} else {
			VBox card = new VBox(5);

			Text txtTitulo = new Text(evento.getTitulo());
			txtTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

			Text txtData = new Text(evento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
			txtData.setStyle("-fx-font-size: 12px; -fx-fill: #666;");

			Text txtLocal = new Text(evento.getLocal());
			txtLocal.setStyle("-fx-font-size: 12px; -fx-fill: #666;");

			card.getChildren().addAll(txtTitulo, txtData, txtLocal);
			setGraphic(card);
		}
	}
}