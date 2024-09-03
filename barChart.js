import React from 'react';
import { Bar } from 'react-chartjs-2';
import ChartJS from 'chart.js/auto'; // Ensure you import chart.js components
import iconPlugin from './iconPlugin'; // Import your custom plugin

const BarChart = ({ data, options }) => {
  const chartData = {
    labels: ['Label 1', 'Label 2', 'Label 3'], // Your labels
    datasets: [
      {
        label: 'Dataset 1',
        data: [10, 20, 30], // Your data
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        borderColor: 'rgba(75, 192, 192, 1)',
        borderWidth: 1
      }
    ]
  };

  const chartOptions = {
    plugins: {
      legend: {
        display: true
      },
      tooltip: {
        callbacks: {
          label: (context) => `${context.dataset.label}: ${context.raw}`
        }
      }
    },
    scales: {
      x: {
        beginAtZero: true
      },
      y: {
        beginAtZero: true
      }
    },
    // Register your custom plugin
    plugins: {
      iconPlugin
    }
  };

  return <Bar data={chartData} options={chartOptions} />;
};

export default BarChart;
