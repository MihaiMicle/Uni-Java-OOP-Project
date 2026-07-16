package ui.command;

import domain.Session;
import service.SessionService;

public class SessionCommands {

    public static class AddCommand implements Command {
        private final SessionService service;
        private final Session session;

        public AddCommand(SessionService service, int id, int clientId, String date, String time, String desc) {
            this.service = service;
            this.session = new Session(id, clientId, date, time, desc);
        }

        @Override
        public void execute() throws Exception {
            service.addSession(session.getID(), session.getClientID(), session.getDate(), session.getTime(), session.getWorkoutDescription());
        }

        @Override
        public void undo() throws Exception {
            service.deleteSession(session.getID());
        }
    }


    public static class DeleteCommand implements Command {
        private final SessionService service;
        private final Session backupSession;

        public DeleteCommand(SessionService service, Session sessionToDelete) {
            this.service = service;
            this.backupSession = new Session(
                    sessionToDelete.getID(),
                    sessionToDelete.getClientID(),
                    sessionToDelete.getDate(),
                    sessionToDelete.getTime(),
                    sessionToDelete.getWorkoutDescription()
            );
        }

        @Override
        public void execute() throws Exception {
            service.deleteSession(backupSession.getID());
        }

        @Override
        public void undo() throws Exception {
            service.addSession(backupSession.getID(), backupSession.getClientID(), backupSession.getDate(), backupSession.getTime(), backupSession.getWorkoutDescription());
        }
    }


    public static class UpdateCommand implements Command {
        private final SessionService service;
        private final Session oldState;
        private final Session newState;

        public UpdateCommand(SessionService service, Session currentSession, int newId, int newClientId, String newDate, String newTime, String newDesc) {
            this.service = service;
            this.oldState = new Session(
                    currentSession.getID(),
                    currentSession.getClientID(),
                    currentSession.getDate(),
                    currentSession.getTime(),
                    currentSession.getWorkoutDescription()
            );

            this.newState = new Session(newId, newClientId, newDate, newTime, newDesc);
        }

        @Override
        public void execute() throws Exception {
            service.updateSession(newState.getID(), newState.getClientID(), newState.getDate(), newState.getTime(), newState.getWorkoutDescription());
        }

        @Override
        public void undo() throws Exception {
            service.updateSession(oldState.getID(), oldState.getClientID(), oldState.getDate(), oldState.getTime(), oldState.getWorkoutDescription());
        }
    }
}