import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Lightbulb } from 'lucide-react';
import { PredictiveForecast } from '@/types/predict-lite';

interface PredictiveReflectionsLiteProps {
  forecast: PredictiveForecast | null;
  loading?: boolean;
}

export function PredictiveReflectionsLite({ forecast, loading }: PredictiveReflectionsLiteProps) {
  if (!forecast) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Lightbulb className="w-4 h-4" />
            Predictive Insights
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-muted-foreground text-sm">Loading...</div>
        </CardContent>
      </Card>
    );
  }

  const insights = forecast.insights || [];

  if (forecast.fallback || insights.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Lightbulb className="w-4 h-4" />
            Predictive Insights
          </CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground text-sm">
            Predictive insights improve as recurring financial patterns become available
          </p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Lightbulb className="w-4 h-4" />
          Predictive Insights
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        {insights.slice(0, 4).map((insight, index) => (
          <div key={index} className="p-3 rounded-lg bg-muted/50 border border-muted-foreground/10">
            <p className="font-medium text-sm mb-1">{insight.title}</p>
            <p className="text-xs text-muted-foreground">{insight.description}</p>
            <div className="mt-2 flex items-center gap-2">
              <div className="text-xs text-muted-foreground">Weight:</div>
              <div className="flex-1 h-1.5 bg-muted rounded-full overflow-hidden">
                <div
                  className="h-full bg-blue-600"
                  style={{ width: `${Math.round(insight.weight * 100)}%` }}
                />
              </div>
            </div>
          </div>
        ))}
      </CardContent>
    </Card>
  );
}
