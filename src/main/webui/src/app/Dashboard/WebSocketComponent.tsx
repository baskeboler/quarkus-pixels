import * as React from 'react';
import { useEffect, useState } from 'react';



const WebSocketComponent = ({ mapId }) => {
  const [messages, setMessages] = useState<string[]>([]);

  function addMessage(msg: string) {
    setMessages((prevMessages) => [...prevMessages, msg]);
  }

  useEffect(() => {
    const socket = new WebSocket('start-websocket/' + mapId);
    setMessages(() => []);
    socket.onopen = () => {
      console.log('WebSocket connection opened');
    };

    socket.onmessage = (event) => {
      console.log('Message received:', event.data);
      addMessage(event.data);
    };

    socket.onerror = (error) => {
      console.error('WebSocket error:', error);
    };

    socket.onclose = () => {
      console.log('WebSocket connection closed');
    };

    // Cleanup on component unmount
    return () => {
      if (socket.readyState === WebSocket.OPEN) {
        // <-- This is important
        console.log('Closing WebSocket connection');
        socket.close();
      }
    };
  }, [mapId]);

  return (
    <div>
      <h1>WebSocket Messages</h1>
      <ul>
        {messages.map((message, index) => (
          <li key={index}>{message}</li>
        ))}
      </ul>
    </div>
  );
};
export default WebSocketComponent;
