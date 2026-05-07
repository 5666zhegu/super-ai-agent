package com.geek.superaiagent.chatMemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileBaseMemory implements ChatMemory {

    private final String BASE_DIR;

    private static final Kryo kryo = new Kryo();

    static{
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    public FileBaseMemory(String dir){
        this.BASE_DIR = dir;
        File file = new File(dir);
        if(!file.exists()){
            file.mkdirs();
        }

    }
    @Override
    public void add(String conversationId, Message message) {
        List<Message> messages = getOrCreateConversation(conversationId);
        messages.add(message);
        saveConversation(conversationId,messages);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> messageList = getOrCreateConversation(conversationId);
        messageList.addAll(messages);
        saveConversation(conversationId,messageList);
    }

    @Override
    public List<Message> get(String conversationId) {
        List<Message> messages1 = getOrCreateConversation(conversationId);
        return messages1;

    }

    private List<Message> getLastNMessage(String conversationId , int LastN){
        List<Message> allMessage = getOrCreateConversation(conversationId);
        return allMessage.stream()
                .skip(Math.max(0, allMessage.size()-LastN)
                ).toList();

    }

    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if(file.exists()){
            file.delete();
        }

    }

    public List<Message> getOrCreateConversation(String conversationId){
        File file = getConversationFile(conversationId);
        ArrayList<Message> message = new ArrayList<>();
        if(file.exists()){
            try(Input input = new Input(new FileInputStream(file))){
                message = kryo.readObject(input, ArrayList.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    public void saveConversation(String conversationId,List<Message> messages){
        File file = getConversationFile(conversationId);
        try(Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output,messages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getConversationFile(String conversationId){
        return new File(BASE_DIR,conversationId+".kryo");
    }
}
