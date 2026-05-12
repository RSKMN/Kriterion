import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { AlertCircle, TrendingDown, TrendingUp } from 'lucide-react';
import { PredictiveForecast } from '@/types/predict-lite';

interface OverspendingForecastCardLiteProps {
  forecast: PredictiveForecast | null;
  loading?: boolean;
}

export function OverspendingForecastCardLite({ forecast, loading }: OverspendingForecastCardLiteProps) {
  if (!forecast) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <AlertCircle className="w-4 h-4" />
            Overspending Risk
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-muted-foreground text-sm">Loading...</div>
        </CardContent>
      </Card>
    );
  }

  const riskColor =
    forecast.overspendingRisk === 'high'
      ? 'text-red-600'
      : forecast.overspendingRisk === 'moderate'
        ? 'text-amber-600'
        : 'text-green-600';

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <AlertCircle className="w-4 h-4" />
          Overspending Risk
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex items-baseline gap-3">
          <span className={`text-2xl font-bold capitalize ${riskColor}`}>
            {forecast.overspendingRisk}
          </span>
          <span className="text-muted-foreground text-sm">risk level</span>
        </div>

        {forecast.forecastDaysRemaining !== null && (
          <div className="text-sm text-muted-foreground">
            ~{forecast.forecastDaysRemaining} days remaining at current pace
          </div>
        )}

        <div className="flex items-center gap-2">
          <div className="text-xs text-muted-foreground">Confidence:</div>
          <div className="flex-1 h-2 bg-muted rounded-full overflow-hidden">
            <div
              className="h-full bg-blue-600"
              style={{ width: `${Math.round(forecast.confidence * 100)}%` }}
            />
          </div>
          <span className="text-xs font-medium">{Math.round(forecast.confidence * 100)}%</span>
        </div>
      </CardContent>
    </Card>
  );
}
