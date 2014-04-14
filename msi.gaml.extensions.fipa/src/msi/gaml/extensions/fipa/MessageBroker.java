/*********************************************************************************************
 * 
 * 
 * 'MessageBroker.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.extensions.fipa;

import java.util.*;
import msi.gama.kernel.experiment.AgentScheduler;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.GamaHelper;

/**
 * The Class MessageBroker.
 * 
 * @author drogoul
 */
public class MessageBroker {

	/** The messages to deliver. */
	private final Map<IAgent, List<Message>> messagesToDeliver = new HashMap<IAgent, List<Message>>();

	/** Centralized storage of Conversations and Messages to facilitate Garbage Collection */
	private final Map<IAgent, ConversationsMessages> conversationsMessages =
		new HashMap<IAgent, ConversationsMessages>();

	/** The instance. */
	private static MessageBroker instance;

	/**
	 * @throws GamaRuntimeException Deliver message.
	 * 
	 * @param m the m
	 * 
	 * @throws GamlException the gaml exception
	 */
	public IList<Message> deliverMessagesFor(final IScope scope, final IAgent a) throws GamaRuntimeException {
		final List<Message> messagesForA = messagesToDeliver.get(a);
		if ( messagesForA == null ) { return GamaList.EMPTY_LIST; }

		IList<Message> successfulDeliveries = new GamaList<Message>();
		IList<Message> failedDeliveries = new GamaList<Message>();

		for ( Message m : messagesForA ) {
			Conversation conv = m.getConversation();
			try {
				conv.addMessage(scope, m);
			} catch (GamaRuntimeException e) {
				failedDeliveries.add(m);
				failureMessageInReplyTo(m);
				conv.end();
				throw e;
			} finally {
				if ( !failedDeliveries.contains(m) ) {
					successfulDeliveries.add(m);
				}
			}
		}

		messagesToDeliver.remove(a);
		return successfulDeliveries;
	}

	/**
	 * @throws GamaRuntimeException Deliver failure in reply to.
	 * 
	 * @param m the m
	 * 
	 * @throws GamlException the gaml exception
	 */
	protected Message failureMessageInReplyTo(final Message m) throws GamaRuntimeException {
		if ( m.getPerformative() == FIPAConstants.Performatives.FAILURE ) { return null; }

		final Message f = new Message();
		f.setSender(null);
		final GamaList<IAgent> receivers = new GamaList();
		receivers.add(m.getSender());
		f.setReceivers(receivers);
		f.setPerformative(FIPAConstants.Performatives.FAILURE);
		f.setConversation(m.getConversation());
		f.setContent(m.getContent());
		return f;
	}

	/**
	 * Schedule for delivery.
	 * 
	 * @param m the m
	 */
	public void scheduleForDelivery(final IScope scope, final Message m) {
		for ( IAgent a : m.getReceivers().iterable(scope) ) {
			scheduleForDelivery(m.clone(), a);
		}
	}

	private void scheduleForDelivery(final Message m, final IAgent agent) {
		List<Message> messages = messagesToDeliver.get(agent);
		if ( messages == null ) {
			messages = new ArrayList();
			messagesToDeliver.put(agent, messages);
		}
		messages.add(m);
	}

	/**
	 * @throws GamaRuntimeException Schedule for delivery.
	 * 
	 * @param m the m
	 * @param protocol the protocol
	 * 
	 * @throws UnknownProtocolException the unknown protocol exception
	 * @throws ProtocolErrorException the protocol error exception
	 * @throws GamlException the gaml exception
	 */
	public void scheduleForDelivery(final IScope scope, final Message m, final Integer protocol) {
		Conversation conv;
		conv = new Conversation(scope, protocol, m);
		m.setConversation(conv);
		scheduleForDelivery(scope, m);
	}

	/**
	 * Gets the single instance of MessageBroker.
	 * 
	 * @param sim the sim
	 * 
	 * @return single instance of MessageBroker
	 */
	public static MessageBroker getInstance() {
		if ( instance == null ) {
			instance = new MessageBroker();
			AgentScheduler s = GAMA.getSimulation().getScheduler();

			s.insertEndAction(new GamaHelper() {

				@Override
				public Object run(final IScope scope) throws GamaRuntimeException {
					instance.manageConversationsAndMessages();
					return null;
				}
			});
			s.insertDisposeAction(new GamaHelper() {

				@Override
				public Object run(final IScope scope) throws GamaRuntimeException {
					instance.schedulerDisposed();
					return null;
				}
			});
		}
		return instance;
		// TODO Il faudrait pouvoir en gérer plusieurs (par simulation)
	}

	public void dispose() {
		messagesToDeliver.clear();
	}

	public IList<Message> getMessagesFor(final IAgent agent) {
		if ( !conversationsMessages.containsKey(agent) ) {
			ConversationsMessages cm = new ConversationsMessages();
			conversationsMessages.put(agent, cm);
			return cm.messages;
		}

		return conversationsMessages.get(agent).messages;
	}

	public List<Conversation> getConversationsFor(final IAgent agent) {
		if ( !conversationsMessages.containsKey(agent) ) {
			ConversationsMessages cm = new ConversationsMessages();
			conversationsMessages.put(agent, cm);
			return cm.conversations;
		}

		return conversationsMessages.get(agent).conversations;
	}

	public void addConversation(final Conversation c) {
		List<IAgent> members = new GamaList<IAgent>();
		members.add(c.getIntitiator());
		for ( IAgent m : (GamaList<IAgent>) c.getParticipants() ) {
			members.add(m);
		}

		for ( IAgent m : members ) {
			addConversation(m, c);
		}
	}

	private void addConversation(final IAgent a, final Conversation c) {
		ConversationsMessages cm = new ConversationsMessages();
		cm.conversations.add(c);
		conversationsMessages.put(a, cm);
	}

	/**
	 * @throws GamaRuntimeException Removes the already ended conversations.
	 */
	public void manageConversationsAndMessages() throws GamaRuntimeException {

		// remove ended conversations
		List<Conversation> conversations;
		List<Conversation> endedConversations = new GamaList<Conversation>();
		for ( IAgent a : conversationsMessages.keySet() ) {
			if ( a.dead() ) {
				ConversationsMessages cm = conversationsMessages.get(a);
				cm.conversations.clear();
				cm.messages.clear();
				cm.conversations = null;
				cm.messages = null;
				conversationsMessages.remove(a);
				return;
			}
			conversations = conversationsMessages.get(a).conversations;
			endedConversations.clear();

			for ( Conversation c : conversations ) {
				if ( c.isEnded() && c.areMessagesRead() ) {
					endedConversations.add(c);
				}
			}

			for ( final Conversation endedConv : endedConversations ) {
				endedConv.dispose();
			}
			conversations.removeAll(endedConversations);
		}
	}

	class ConversationsMessages {

		IList<Conversation> conversations;
		IList<Message> messages;

		ConversationsMessages() {
			this.conversations = new GamaList<Conversation>();
			this.messages = new GamaList<Message>();
		}
	}

	public void schedulerDisposed() {
		messagesToDeliver.clear();

		ConversationsMessages cm;
		for ( IAgent a : conversationsMessages.keySet() ) {
			cm = conversationsMessages.get(a);
			cm.conversations.clear();
			cm.conversations = null;
			cm.messages.clear();
			cm.messages = null;
		}
		conversationsMessages.clear();
		instance = null;
	}
}
