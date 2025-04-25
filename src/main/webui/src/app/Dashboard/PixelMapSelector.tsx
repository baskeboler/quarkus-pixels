import React from 'react';

const PixelMapSelector = ({ setPixelMapId }) => {
  let [inputText, setInputText] = React.useState('');

  return (
    <div className="pixel-map-selector">
      <h2>Select a Pixel Map</h2>
      <input
        type="text"
        placeholder="Enter Pixel Map Id"
        onChange={(e) => setInputText(e.target.value)}
        value={inputText}
      />
      <button
        onClick={() => {
          console.log('Selected Pixel Map Id:', inputText);
          setPixelMapId(inputText);
        }}
      >
        Set Pixel Map Id
      </button>
    </div>
  );
};

export default PixelMapSelector;
