import { Chart as ChartJS, BarElement, CategoryScale, LinearScale, Title, Tooltip, Legend } from 'chart.js';

// Register components
ChartJS.register(
  BarElement,
  CategoryScale,
  LinearScale,
  Title,
  Tooltip,
  Legend
);

const iconPlugin = {
  id: 'iconPlugin',
  afterDatasetsDraw(chart, args, options) {
    const { ctx, data } = chart;
    const { datasets } = data;

    datasets.forEach((dataset) => {
      const { data: datasetData, label } = dataset;
      datasetData.forEach((value, index) => {
        const meta = chart.getDatasetMeta(dataset.index);
        const bar = meta.data[index];

        if (bar) {
          const { width, x, y } = bar;
          const iconSize = 20; // Adjust size as needed
          
          // Draw your icon here
          const img = new Image();
          img.src = 'path/to/your/icon.svg'; // Replace with your icon path or URL

          img.onload = () => {
            ctx.drawImage(img, x - iconSize / 2, y - iconSize, iconSize, iconSize);
          };
        }
      });
    });
  }
};

export default iconPlugin;
