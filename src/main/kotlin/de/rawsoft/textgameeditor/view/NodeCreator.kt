package de.rawsoft.textgameeditor.view

import de.rawsoft.textgameeditor.JavaScriptCodeArea
import de.rawsoft.textgameeditor.controller.NodeController
import de.rawsoft.textgameeditor.controller.frequencyOf
import de.rawsoft.textgameeditor.game.GameNodeModel
import org.fxmisc.flowless.VirtualizedScrollPane
import tornadofx.*

class NodeCreator(val path: String, val nodeModel: GameNodeModel, val onSave: (nodeModel: GameNodeModel) -> Unit) : View("Node Creator") {
    val nodeController: NodeController by inject()

    override val root = form {
        stylesheets.add(JavaScriptCodeArea::class.java.getResource("/keywords.css").toExternalForm())
        fieldset("Node") {
            field("Name:") {
                textfield(nodeModel.name).validator {
                    if(nodeController.nodes.keys.filter { it.startsWith(path) && it.frequencyOf('.') > path.frequencyOf('.') }.map { it.substring(it.lastIndexOf('.') + 1) }.any { it == nodeModel.name.value })
                        error("Diesen Namen gibt es bereits")
                    else if(it.isNullOrBlank())
                        error("Ungültiger Wert")
                    else
                        null
                }
            }
            field("Title:") {
                textfield(nodeModel.title).validator {
                    if(it.isNullOrBlank()) error("Ungültiger Wert") else null
                }
            }
            field("Message:") {
                textarea(nodeModel.message).validator {
                    if(it.isNullOrBlank()) error("Ungültiger Wert") else null
                }
            }
        }
        fieldset("ActionScript") {
            field("Script:") {
                this += VirtualizedScrollPane(JavaScriptCodeArea())
            }
        }
        button("Save") {
            enableWhen(nodeModel.valid)
            setOnAction {
                nodeModel.commit()
                onSave.invoke(nodeModel)
                close()
            }
        }

        nodeModel.validate(decorateErrors = true)
    }
}