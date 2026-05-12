import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { AlertTriangle } from 'lucide-react';
import { PredictiveForecast } from '@/types/predict-lite';

interface EndOfMonthRiskCardLiteProps {
  forecast: PredictiveForecast | null;
  loading?: boolean;
}

export function EndOfMonthRiskCardLite({ forecast, loading }: EndOfMonthRiskCardLiteProps) {
  if (!forecast) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <AlertTriangle className="w-4 h-4" />
            End-of-Month Risk
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-muted-foreground text-sm">Loading...</div>
        </CardContent>
      </Card>
    );
  }

  // Find the end-of-month insight if available
  const endOfMonthInsight = forecast.insights?.find(i => i.category === 'end_of_month');
  const hasPattern = endOfMonthInsight !== undefined;

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <AlertTriangle className="w-4 h-4" />
          End-of-Month Risk
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div>
          {hasPattern ? (
            <div className="space-y-2">
              <p className="text-sm font-medium text-amber-700">
                Elevated spending pattern detected
              </p>
              <p className="text-sm text-muted-foreground">
                Historical data shows increased expense activity in the final week of months
              </p>
            </div>
          ) : (
            <div className="text-sm text-muted-foreground">
              No significant end-of-month spending spike pattern detected
            </div>
          )}
        </div>

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
