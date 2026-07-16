package ui.command;

import java.util.Stack;

public class UndoRedoManager {
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    public void executeCommand(Command command) throws Exception {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    public void undo() throws Exception {
        if(!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    public void redo() throws Exception {
        if(!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }
}
