import * as React from 'react';
import { Content, PageSection, Title } from '@patternfly/react-core';
import WebSocketComponent from '@app/Dashboard/WebSocketComponent';
import PixelMapSelector from '@app/Dashboard/PixelMapSelector';
import { useEffect } from 'react';

interface ColorGridProps {
  width: number;
  height: number;
  colors: string[]; // Array of hex color values
}

const ColorGrid: React.FunctionComponent<ColorGridProps> = ({ width, height, colors }) => {
  if (colors.length !== width * height) {
    throw new Error('The number of colors must match width x height.');
  }

  const gridStyle: React.CSSProperties = {
    display: 'grid',
    gridTemplateColumns: `repeat(${width}, 1fr)`,
    gridTemplateRows: `repeat(${height}, 1fr)`,
    // gap: '2px', // Optional gap between squares
  };

  const squareStyle: React.CSSProperties = {
    width: '100%',
    height: '20px',
  };

  return (
    <div style={gridStyle}>
      {colors.map((color, index) => (
        <div
          key={index}
          style={{
            ...squareStyle,
            backgroundColor: `#${color.substr(0, 6)}`,
          }}
        ></div>
      ))}
    </div>
  );
};

async function getMaps() {
  let request = new Request('api/pixels', {
    method: 'POST',
  });

  let maps = await fetch(request);
  let newPixelMap = await maps.json();
  console.log(`got new pixel map: ${newPixelMap.id}`);
  console.log(newPixelMap);
  return newPixelMap;
}

async function getPMap(id) {
  let request = new Request(`api/pixels/${id}`, {
    method: 'GET',
  });

  let map = await fetch(request);
  let newPixelMap = await map.json();
  console.log(`got pixel map: ${newPixelMap.id}`);
  console.log(newPixelMap);
  return newPixelMap;
}

let theId = 'e393dc51-e5f0-46fd-be10-d6fb3546d08d';

let m;

const Dashboard: React.FunctionComponent = () => {
  const [mapId, setMapId] = React.useState<string>(theId);
  useEffect( () => {
     getPMap(mapId).then(value => {
      console.log(`got pixel map: ${value.id}`);
      console.log(value);
      m = value;
     });
  }, [mapId]);
  // @ts-ignore
  return (
    // await getMaps();
    <PageSection hasBodyWrapper={false}>
      <Title headingLevel="h1" size="lg">
        Dashboard Page Title!
      </Title>
      <Content>
        <PixelMapSelector setPixelMapId={setMapId} />
        <WebSocketComponent mapId={mapId} />
        <ColorGrid width={m.width} height={m.height} colors={m.pixels} />
      </Content>
    </PageSection>
  );
};

export { Dashboard };
