import React from 'react';
import { ReflectionResponse } from '@/services/financial-reflection.service';
import { Lightbulb } from 'lucide-react';

interface ReflectionInsightsCardProps {
  reflections: ReflectionResponse[];
}

export const ReflectionInsightsCard: React.FC<ReflectionInsightsCardProps> = ({ reflections }) => {
  if (!reflections || reflections.length === 0) {
    return (
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <h3 className="text-lg font-semibold text-gray-900 flex items-center gap-2 mb-4">
          <Lightbulb className="w-5 h-5 text-yellow-600" />
          Reflective Insights
        </h3>
        <p className="text-sm text-gray-500 italic">
          Generate more transaction history to unlock behavioral insights and reflections.
        </p>
      </div>
    );
  }

  const getToneColor = (tone: string) => {
    switch (tone) {
      case 'positive':
        return 'border-l-4 border-emerald-400 bg-emerald-50';
      case 'cautionary':
        return 'border-l-4 border-amber-400 bg-amber-50';
      case 'analytical':
        return 'border-l-4 border-blue-400 bg-blue-50';
      case 'neutral':
        return 'border-l-4 border-gray-400 bg-gray-50';
      default:
        return 'border-l-4 border-gray-300 bg-gray-50';
    }
  };

  const getCategoryIcon = (category: string) => {
    switch (category) {
      case 'rhythm':
        return '🔄';
      case 'density':
        return '📊';
      case 'structure':
        return '🏗️';
      case 'stability':
        return '⚖️';
      case 'temporal':
        return '⏰';
      case 'pattern':
        return '🔍';
      default:
        return '✦';
    }
  };

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6">
      <h3 className="text-lg font-semibold text-gray-900 flex items-center gap-2 mb-4">
        <Lightbulb className="w-5 h-5 text-yellow-600" />
        Reflective Insights
      </h3>

      <div className="space-y-3">
        {reflections.map((reflection, idx) => (
          <div
            key={idx}
            className={`p-4 rounded-lg ${getToneColor(reflection.tone)} transition-all hover:shadow-sm`}
          >
            <div className="flex items-start justify-between mb-2">
              <div className="flex items-start gap-2 flex-1">
                <span className="text-lg mt-0.5">{getCategoryIcon(reflection.category)}</span>
                <div>
                  <h4 className="font-semibold text-gray-900">{reflection.title}</h4>
                  {reflection.isSignificant && (
                    <span className="inline-block text-xs font-medium text-gray-600 mt-1">
                      • Significant
                    </span>
                  )}
                </div>
              </div>
              <div className="text-right">
                <div className="flex items-center gap-1">
                  <div className="h-1.5 w-24 bg-gray-200 rounded overflow-hidden">
                    <div
                      className="h-1.5 bg-indigo-500 rounded transition-all"
                      style={{ width: `${reflection.weight * 100}%` }}
                    />
                  </div>
                  <span className="text-xs text-gray-500 w-8 text-right">
                    {Math.round(reflection.weight * 100)}%
                  </span>
                </div>
              </div>
            </div>
            <p className="text-sm text-gray-700 leading-relaxed">{reflection.observation}</p>
          </div>
        ))}
      </div>
    </div>
  );
};
