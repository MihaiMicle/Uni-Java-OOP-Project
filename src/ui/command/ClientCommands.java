package ui.command;

import domain.Client;
import service.ClientService;

public class ClientCommands {

    public static class AddCommand implements Command {
        private final ClientService service;
        private final Client client;

        public AddCommand(ClientService service, int id, String fn, String ln, String e, String p) {
            this.service = service;
            this.client = new Client(id,fn,ln,e,p);
        }

        @Override
        public void execute() throws Exception {
            service.addClient(client.getID(), client.getFirstname(), client.getLastname(), client.getEmail(), client.getPhone());
        }

        @Override
        public void undo() throws Exception {
            service.deleteClient(client.getID());
        }
    }

    public static class DeleteCommand implements Command {
        private final ClientService service;
        private final Client backupClient;

        public DeleteCommand(ClientService service, Client clientToDelete) {
            this.service = service;
            this.backupClient = new Client(
                    clientToDelete.getID(),
                    clientToDelete.getFirstname(),
                    clientToDelete.getLastname(),
                    clientToDelete.getEmail(),
                    clientToDelete.getPhone()
            );
        }

        @Override
        public void execute() throws Exception {
            service.deleteClient(backupClient.getID());
        }

        @Override
        public void undo() throws Exception {
            service.addClient(backupClient.getID(), backupClient.getFirstname(),
                              backupClient.getLastname(), backupClient.getEmail(), backupClient.getPhone());
        }
    }

    public static class UpdateCommand implements Command {
        private final ClientService service;
        private final Client initialState;
        private final Client newState;

        public UpdateCommand(ClientService service, Client current, int id, String fn, String ln, String e, String p ) {
            this.service = service;
            this.initialState = new Client(current.getID(), current.getFirstname(), current.getLastname(), current.getEmail(), current.getPhone());
            this.newState = new Client(id,fn,ln,e,p);
        }

        @Override
        public void execute() throws Exception {
            service.updateClient(newState.getID(), newState.getFirstname(), newState.getLastname(), newState.getEmail(), newState.getPhone());
        }

        @Override
        public void undo() throws Exception {
            service.updateClient(initialState.getID(), initialState.getFirstname(), initialState.getLastname(), initialState.getEmail(), initialState.getPhone());
        }
    }
}
